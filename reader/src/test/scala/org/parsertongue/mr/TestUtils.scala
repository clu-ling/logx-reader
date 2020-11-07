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
 
  case class GeneralMentionTestCase(
    labels: Seq[LabelTestCase], 
    text: String,
    mentionSpan: TextTestCase, 
    args: List[ArgTestCase] = Nil, 
    foundBy: Option[String] = None
  ) extends MentionTestCase

  case class NegativeMentionTestCase(
    labels: Seq[NegativeLabelTestCase], 
    text: String,
    mentionSpan: NegativeTextTestCase, 
    args: List[ArgTestCase] = Nil, 
    foundBy: Option[String] = None
  ) extends MentionTestCase

  case class ArgTestCase(
    role: RoleTestCase, 
    labels: Seq[LabelTestCase], 
    text: TextTestCase
  ) {
    def check(parent: Mention): Boolean = {
      role.check(parent) && 
      parent.arguments.getOrElse(role.role, Nil).forall{ arg =>
        //check labels
        labels.forall { lbl => lbl.check(arg) } && 
        //check text
        text.check(arg)
      } 
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
      case nm: NegativeMentionTestCase =>
        mentions.forall { m => nm.check(m) }
      case generic =>
        mentions.exists { m => generic.check(m) }
    }

    // if (! success) {
    //   testCase match {
    //     case pc: PositiveEventTestCase =>
    //       println(s"${Console.RED} PositiveTestCase failed for '${pc.text}'${Console.RESET}")
    //       if ( ! mentions.exists{ m => pc.foundBy.getOrElse(m.foundBy) == m.foundBy } ) {
    //         println(s"\t${Console.RED} No Mention found by '${pc.foundBy.get}'${Console.RESET}")
    //       }
    //       if (! mentions.exists{ m => checkLabels(pc.labels, m) } ) {
    //         println(s"\t${Console.RED} Labels (${pc.labels.mkString(",")}) missing${Console.RESET}")
    //       }
    //       pc.args.foreach{ tcArg => 
    //         // check for role
    //         if (! mentions.exists{ m => m.arguments.contains(tcArg.role) } ) {
    //           println(s"\t${Console.RED} Role '${tcArg.role}' missing${Console.RESET}")
    //         }
    //         // check arg labels
    //         if (! mentions.exists{ m => 
    //               m.arguments.getOrElse(tcArg.role, Nil)
    //                 .exists{ ma => checkLabels(tcArg.labels, ma) }
    //             } 
    //           ) {
    //           println(s"\t${Console.RED} Arg labels '${tcArg.labels.mkString(", ")}' missing${Console.RESET}")
    //         }
    //         // check arg text
    //         if (! mentions.exists{ m => 
    //                 m.arguments.getOrElse(tcArg.role, Nil)
    //                   .exists{ ma => ma.text == tcArg.text }
    //               } 
    //           ) {
    //           println(s"\t${Console.RED} Arg text '${tcArg.text}' missing${Console.RESET}")
    //         }
    //       }
    //     case nc: NegativeEventTestCase =>
    //       println(s"${Console.RED} NegativeTestCase failed for '${nc.text}'${Console.RESET}")
    //       if ((nc.foundBy.nonEmpty) && ( mentions.exists{ m => nc.foundBy.getOrElse(m.foundBy) == m.foundBy } ) ){
    //         println(s"\t${Console.RED} Mention incorrectly found by '${nc.foundBy.get}'${Console.RESET}")
    //       }
    //       //fill in rest
    //       if (mentions.exists{ m => checkLabels(nc.labels, m) } ) {
    //         println(s"\t${Console.RED} Labels (${nc.labels.mkString(",")}) incorrectly present${Console.RESET}")
    //       }
    //       nc.args.foreach{ tcArg => 
    //         // check for role
    //         if (mentions.exists{ m => m.arguments.contains(tcArg.role) } ) {
    //           println(s"\t${Console.RED} Role '${tcArg.role}' incorrectly present${Console.RESET}")
    //         }
    //         // check arg labels
    //         if (mentions.exists{ m => 
    //               m.arguments.getOrElse(tcArg.role, Nil)
    //                 .exists{ ma => checkLabels(tcArg.labels, ma) }
    //             } // not sure what to do in above block in neg case
    //           ) {
    //           println(s"\t${Console.RED} Arg labels '${tcArg.labels.mkString(", ")}' incorrectly found${Console.RESET}")
    //         }
    //         // check arg text
    //         if (mentions.exists{ m => 
    //                 m.arguments.getOrElse(tcArg.role, Nil)
    //                   .exists{ ma => ma.text == tcArg.text }
    //               } 
    //           ) {
    //           println(s"\t${Console.RED} Arg text '${tcArg.text}' incorrectly found${Console.RESET}")
    //         }
    //       } 
    //   }
   
    // }
    success
  }
}
