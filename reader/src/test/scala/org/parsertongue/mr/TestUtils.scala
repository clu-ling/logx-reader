package org.parsertongue.mr

import org.parsertongue.mr.MachineReadingSystem
import org.parsertongue.mr.logx.odin.DisplayUtils
//import org.parsertongue.mr.processor.ProxiedProcessor
import org.clulab.odin.{ Mention, TextBoundMention }
//import org.clulab.processors.{ Document => CluDocument, Processor }
import org.clulab.serialization.json.JSONSerializer
import org.json4s.jackson.JsonMethods.parse


object TestUtils {
  // use default conf
  val system: MachineReadingSystem = MachineReadingSystem()

  //val proc: Processor = new ProxiedProcessor()
  trait MentionTestCase {
    val labels: Seq[LabelTestCase] 
    val text: String
    val mentionSpan: TextTestCase
    val args: List[ArgTestCase] 
    val foundBy: Option[String]
    def check(m: Mention): Boolean = {
      // check mentionSpan
      mentionSpan.check(m) &&
      // check foundBy
      (foundBy.getOrElse(m.foundBy) == m.foundBy) && 
      // check labels
      labels.forall { lbl => lbl.check(m) } &&
      // check args
      args.forall { arg => arg.check(m) }
    }
  }
 
  case class ExistsMentionTestCase(
    labels: Seq[LabelTestCase], 
    text: String,
    mentionSpan: TextTestCase,
    args: List[ArgTestCase] = Nil, //removed pos polarity; revert or change doc
    foundBy: Option[String] = None
  ) extends MentionTestCase

  case class ForAllMentionTestCase(
    labels: Seq[NegativeLabelTestCase], 
    text: String,
    mentionSpan: NegativeTextTestCase,
    args: List[NegativeArgTestCase] = Nil, //added neg polarity
    foundBy: Option[String] = None
  ) extends MentionTestCase

  trait ArgTestCase {
    val role: RoleTestCase
    val labels: Seq[LabelTestCase]
    val text: TextTestCase
    def check(m: Mention): Boolean
  }

  case class PositiveArgTestCase(
    role: RoleTestCase, 
    labels: Seq[LabelTestCase], 
    text: TextTestCase
  ) extends ArgTestCase {
    def check(parent: Mention): Boolean = {
      role.check(parent) && 
      parent.arguments.getOrElse(role.role, Nil).exists{ arg => //altered from .forall. matches old checkArg
        //check labels
        labels.forall { lbl => lbl.check(arg) } && 
        //check text
        text.check(arg)
      } 
    } 
  }

  case class NegativeArgTestCase(
    role: PositiveRoleTestCase, 
    labels: Seq[PositiveLabelTestCase], 
    text: PositiveTextTestCase
  ) extends ArgTestCase {
    def check(parent: Mention): Boolean = {
      ! (role.check(parent) && 
      parent.arguments.getOrElse(role.role, Nil).forall{ arg =>
        //check labels
        labels.forall { lbl => lbl.check(arg) } && 
        //check text
        text.check(arg) 
      } )
    } 
  }

  trait TextTestCase {
    val text: String
    def check(m: Mention): Boolean
  }

  case class PositiveTextTestCase(
    text: String
  ) extends TextTestCase {
    def check(m: Mention): Boolean = m.text == text
  }

  case class NegativeTextTestCase(
    text: String
  ) extends TextTestCase {
    def check(m: Mention): Boolean = m.text != text
  }

  trait RoleTestCase{
    val role: String
    def check(parent: Mention): Boolean
  }

  case class PositiveRoleTestCase(
    role: String
  ) extends RoleTestCase {
    def check(parent: Mention): Boolean = parent.arguments.contains(role)
  }

  case class NegativeRoleTestCase(
    role: String
  ) extends RoleTestCase {
    def check(parent: Mention): Boolean = ! parent.arguments.contains(role)
  }

  trait LabelTestCase{
    val label: String
    def check(m: Mention): Boolean
  }

  case class PositiveLabelTestCase(
    label: String
  ) extends LabelTestCase {
    def check(m: Mention): Boolean = m matches label  
  }

  case class NegativeLabelTestCase(
    label: String
  ) extends LabelTestCase {
    def check(m: Mention): Boolean = !(m matches label)
  }
  
  def checkMention(testCase: MentionTestCase, mentions: Seq[Mention]): Boolean = {
    val success = testCase match {
      case fm: ForAllMentionTestCase =>
        mentions.forall { m => fm.check(m) } //removed negation
      case em: ExistsMentionTestCase =>
        mentions.exists { m => em.check(m) }
    }

    if (! success) {
      testCase match {
        case em: ExistsMentionTestCase =>
          println(s"${Console.RED} Exists Mention test failed for '${em.text}'${Console.RESET}")
          if ( ! mentions.exists{ m => em.foundBy.getOrElse(m.foundBy) == m.foundBy } ) {
            println(s"\t${Console.RED} No Mention found by '${em.foundBy.get}'${Console.RESET}")
          }
          if (! mentions.exists{ m => em.labels.forall { lbl => lbl.check(m) } } ) {
            println(s"\t${Console.RED} ${em.labels.mkString(",")} failed.${Console.RESET}")
          }
          em.args.foreach{ tcArg => 
            // check for role
            if (! mentions.exists{ m => m.arguments.contains(tcArg.role.role) } ) {
              println(s"\t${Console.RED} Role test failed for '${tcArg.role.role}'${Console.RESET}")
            }
            // check arg labels
            if (! mentions.exists{ m => 
                  m.arguments.getOrElse(tcArg.role.role, Nil)
                    .exists{ ma => tcArg.labels.forall { lbl => lbl.check(ma) } 
                    }
                } 
              ) {
              println(s"\t${Console.RED} Arg label test(s) failed: '${tcArg.labels.mkString(", ")}' ${Console.RESET}")
            }
            // check arg text
            if (! mentions.exists{ m => 
                    m.arguments.getOrElse(tcArg.role.role, Nil)
                      .exists{ ma => tcArg.check(ma) }
                  } 
              ) {
              println(s"\t${Console.RED} Arg text test failed for '${tcArg.text}'${Console.RESET}")
            }
          }
        case fm: ForAllMentionTestCase =>
          println(s"${Console.RED} ForAllTestCase failed for '${fm.text}'${Console.RESET}")
          if ((fm.foundBy.nonEmpty) && ( mentions.exists{ m => fm.foundBy.getOrElse(m.foundBy) == m.foundBy } ) ){
            println(s"\t${Console.RED} FoundBy test failed for '${fm.foundBy.get}'${Console.RESET}")
          }
          //fill in rest
          if (mentions.exists{ m => fm.labels.forall { lbl => lbl.check(m) } } ) {
            println(s"\t${Console.RED} One of (${fm.labels.mkString(",")}) failed.${Console.RESET}")
          }
          fm.args.foreach{ tcArg => 
            // check for role
            if (mentions.exists{ m => m.arguments.contains(tcArg.role.role) } ) {
              println(s"\t${Console.RED} Role incorrectly found: '${tcArg.role.role}'${Console.RESET}")
            }
            // check arg labels
            if (mentions.exists{ m => 
                  m.arguments.getOrElse(tcArg.role.role, Nil)
                    .exists{ ma => tcArg.labels.forall { lbl => lbl.check(ma) } }
                } 
              ) {
              println(s"\t${Console.RED} Arg label incorrectly found: '${tcArg.labels.mkString(", ")}'${Console.RESET}")
            }
            // check arg text
            if (mentions.exists{ m => 
                    m.arguments.getOrElse(tcArg.role.role, Nil)
                      .exists{ ma => tcArg.check(ma) }
                  } 
              ) {
              println(s"\t${Console.RED} Arg text test failed for  '${tcArg.text}'${Console.RESET}")
            }
          } 
      }  
    }
    success
  }
}
