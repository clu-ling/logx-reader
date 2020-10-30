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

  trait EventTestCase {
    val labels: Seq[String]
    val text: String
    val args: List[ArgTestCase]
    val foundBy: Option[String]
  }

  case class PositiveEventTestCase(
    labels: Seq[String], 
    text: String, 
    args: List[ArgTestCase], 
    foundBy: Option[String] = None
  ) extends EventTestCase

  case class NegativeEventTestCase(
    labels: Seq[String], 
    text: String, 
    args: List[ArgTestCase], 
    foundBy: Option[String] = None
  ) extends EventTestCase

  /**
  * labels: labels to verify
  */
  trait ArgTestCase {
    val role: String
    val labels: Seq[String]
    val text: String
  }

  case class PositiveArgTestCase(
    role: String, 
    labels: Seq[String], 
    text: String
  ) extends ArgTestCase

  case class NegativeArgTestCase(
    role: String, 
    labels: Seq[String], 
    text: String
  ) extends ArgTestCase

  trait EntityTestCase {
    val labels: Seq[String]
    val text: String
  }

  case class PositiveEntityTestCase(
    labels: Seq[String],
    text: String
  ) extends EntityTestCase

  case class NegativeEntityTestCase(
    labels: Seq[String],
    text: String
  ) extends EntityTestCase

  def checkEntity(testCase: EntityTestCase, mentions: Seq[Mention]): Boolean = {
    val success = mentions.exists{ m =>
      // check labels found
      testCase match {
        case pc: PositiveEntityTestCase =>
          checkLabels(pc.labels, m) &&
          m.text == pc.text
        case nc: NegativeEntityTestCase =>
          (checkLabels(nc.labels, m) == false) &&
          m.text != nc.text
      }
      //checkLabels(testCase.labels, m) &&
      //m.text == testCase.text
    }
    if (! success) {
      testCase match{
        case pc: PositiveEntityTestCase =>
          println(s"${Console.RED} PositiveTestCase failed for '${pc.text}'${Console.RESET}")
          // check labels
          if (! mentions.exists{ m => checkLabels(pc.labels, m) } ) {
            println(s"\t${Console.RED} Labels (${pc.labels.mkString(",")}) missing${Console.RESET}")
          }
          // check text
          if (! mentions.exists(_.text == pc.text) ) {
            println(s"\t${Console.RED} text (${pc.text}) missing${Console.RESET}")
          }
        case nc: NegativeEntityTestCase =>
          println(s"${Console.RED} NegativeTestCase failed for '${nc.text}'${Console.RESET}")
          // check labels
          if (mentions.exists{ m => checkLabels(nc.labels, m) } ) {
            println(s"\t${Console.RED} Labels (${nc.labels.mkString(",")}) incorrectly present${Console.RESET}")
          }
          // check text
          if (mentions.exists(_.text == nc.text) ) {
            println(s"\t${Console.RED} text (${nc.text}) incorrectly present${Console.RESET}")
          }
      }
    }
    success
  }
  // all 'has' -> 'check'
  def checkLabels(labels: Seq[String], m: Mention): Boolean = {
    labels.forall { lbl =>  m matches lbl }
  }
  
  def checkArg(tc: ArgTestCase, m: Mention): Boolean = tc match {
    case pc: PositiveArgTestCase =>
      m.arguments.contains(pc.role) &&
      m.arguments(pc.role).exists{ mentionArg => 
        checkLabels(pc.labels, mentionArg) &&
        mentionArg.text == pc.text
      }  
    case nc: NegativeArgTestCase =>
      (! m.arguments.contains(nc.role)) &&
      m.arguments(nc.role).forall{ mentionArg =>
        (! checkLabels(nc.labels, mentionArg)) &&
        mentionArg.text != nc.text   
      }
  }

  def checkEvent(testCase: EventTestCase, mentions: Seq[Mention]): Boolean = {
    val success = mentions.exists{ m => 
      // check foundBy (if present)
      testCase match {  
        case pc: PositiveEventTestCase => 
          (pc.foundBy.getOrElse(m.foundBy) == m.foundBy) && 
          (checkLabels(pc.labels, m) == true) && 
          pc.args.forall{ tcArg => checkArg(tcArg, m) }
        case nc: NegativeEventTestCase => 
          (nc.foundBy.getOrElse(m.foundBy) != m.foundBy) && 
          (checkLabels(nc.labels, m) == false) && 
          nc.args.forall{ tcArg => checkArg(tcArg, m) }
      } // && 
      // testCase.args.forall{ tcArg => checkArg(tcArg, m) }
    }

    if (! success) {
      testCase match {
        case pc: PositiveEventTestCase =>
          println(s"${Console.RED} PositiveTestCase failed for '${pc.text}'${Console.RESET}")
          if ( ! mentions.exists{ m => pc.foundBy.getOrElse(m.foundBy) == m.foundBy } ) {
            println(s"\t${Console.RED} No Mention found by '${pc.foundBy.get}'${Console.RESET}")
          }
          if (! mentions.exists{ m => checkLabels(pc.labels, m) } ) {
            println(s"\t${Console.RED} Labels (${pc.labels.mkString(",")}) missing${Console.RESET}")
          }
          pc.args.foreach{ tcArg => 
            // check for role
            if (! mentions.exists{ m => m.arguments.contains(tcArg.role) } ) {
              println(s"\t${Console.RED} Role '${tcArg.role}' missing${Console.RESET}")
            }
            // check arg labels
            if (! mentions.exists{ m => 
                  m.arguments.getOrElse(tcArg.role, Nil)
                    .exists{ ma => checkLabels(tcArg.labels, ma) }
                } 
              ) {
              println(s"\t${Console.RED} Arg labels '${tcArg.labels.mkString(", ")}' missing${Console.RESET}")
            }
            // check arg text
            if (! mentions.exists{ m => 
                    m.arguments.getOrElse(tcArg.role, Nil)
                      .exists{ ma => ma.text == tcArg.text }
                  } 
              ) {
              println(s"\t${Console.RED} Arg text '${tcArg.text}' missing${Console.RESET}")
            }
          }
        case nc: NegativeEventTestCase =>
          println(s"${Console.RED} NegativeTestCase failed for '${nc.text}'${Console.RESET}")
          if ( mentions.exists{ m => nc.foundBy.getOrElse(m.foundBy) == m.foundBy } ) {
            println(s"\t${Console.RED} Mention incorrectly found by '${nc.foundBy.get}'${Console.RESET}")
          }
          //fill in rest
          if (mentions.exists{ m => checkLabels(nc.labels, m) } ) {
            println(s"\t${Console.RED} Labels (${nc.labels.mkString(",")}) incorrectly present${Console.RESET}")
          }
          nc.args.foreach{ tcArg => 
            // check for role
            if (mentions.exists{ m => m.arguments.contains(tcArg.role) } ) {
              println(s"\t${Console.RED} Role '${tcArg.role}' incorrectly present${Console.RESET}")
            }
            // check arg labels
            if (mentions.exists{ m => 
                  m.arguments.getOrElse(tcArg.role, Nil)
                    .exists{ ma => checkLabels(tcArg.labels, ma) }
                } // not sure what to do in above block in neg case
              ) {
              println(s"\t${Console.RED} Arg labels '${tcArg.labels.mkString(", ")}' incorrectly found${Console.RESET}")
            }
            // check arg text
            if (mentions.exists{ m => 
                    m.arguments.getOrElse(tcArg.role, Nil)
                      .exists{ ma => ma.text == tcArg.text }
                  } 
              ) {
              println(s"\t${Console.RED} Arg text '${tcArg.text}' incorrectly found${Console.RESET}")
            }
          } 
      }
      

      // val firstLabel: String = testCase.labels.head
      // var subset = mentions.filter(_ matches firstLabel) {
      //   if subset.forall{checkLabels} 
      // }
      // if (subset.nonEmpty) { 
      //   println(s"${Console.RED} ${DisplayUtils.summarizeMentions(subset)}${Console.RESET}") 
      // } else {
      //   println(s"${Console.RED} ${DisplayUtils.summarizeMentions(mentions)}${Console.RESET}")
      // }
    }
    success
  }

}
