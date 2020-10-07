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

  case class EventTestCase(labels: Seq[String], text: String, args: List[ArgTestCase], foundBy: Option[String] = None)

  /**
  * labels: labels to verify
  */
  case class ArgTestCase(role: String, labels: Seq[String], text: String)

  case class EntityTestCase(labels: Seq[String], text: String)

  def hasEntity(testCase: EntityTestCase, mentions: Seq[Mention]): Boolean = {
    val success = mentions.exists{ m =>
      // check labels found
      hasLabels(testCase.labels, m) &&
      m.text == testCase.text
    }
    if (! success) {
      println(s"${Console.RED} testCase failed for '${testCase.text}'${Console.RESET}")
      // check labels
      if (! mentions.exists{ m => hasLabels(testCase.labels, m) } ) {
        println(s"\t${Console.RED} Labels (${testCase.labels.mkString(",")}) missing${Console.RESET}")
      }
      // check text
      if (! mentions.exists(_.text == testCase.text) ) {
        println(s"\t${Console.RED} text (${testCase.text}) missing${Console.RESET}")
      }
    }
    success
  }

  def hasLabels(labels: Seq[String], m: Mention): Boolean = {
    labels.forall { lbl =>  m matches lbl }
  }

  def hasArg(tc: ArgTestCase, m: Mention): Boolean = {
    m.arguments.contains(tc.role) &&
    m.arguments(tc.role).exists{ mentionArg => 
      hasLabels(tc.labels, mentionArg) &&
      mentionArg.text == tc.text
    }
  }

  def hasEvent(testCase: EventTestCase, mentions: Seq[Mention]): Boolean = {
    val success = mentions.exists{ m => 
      // check foundBy (if present)
      testCase.foundBy.getOrElse(m.foundBy) == m.foundBy
      // check if labels found
      if (testCase.foundBy.nonEmpty) {
        testCase.foundBy.get == m.foundBy
      } else {
        true
      } &&
      hasLabels(testCase.labels, m) &&
      testCase.args.forall{ tcArg => hasArg(tcArg, m) }
    }

    if (! success) {
      println(s"${Console.RED} testCase failed for '${testCase.text}'${Console.RESET}")
      if ( ! mentions.exists{ m => testCase.foundBy.getOrElse(m.foundBy) == m.foundBy } ) {
        println(s"\t${Console.RED} No Mention found by '${testCase.foundBy.get}'${Console.RESET}")
      }
      if (! mentions.exists{ m => hasLabels(testCase.labels, m) } ) {
        println(s"\t${Console.RED} Labels (${testCase.labels.mkString(",")}) missing${Console.RESET}")
      }
      testCase.args.foreach{ tcArg => 
        // check for role
        if (! mentions.exists{ m => m.arguments.contains(tcArg.role) } ) {
          println(s"\t${Console.RED} Role '${tcArg.role}' missing${Console.RESET}")
        }
        // check arg labels
        if (! mentions.exists{ m => 
              m.arguments.getOrElse(tcArg.role, Nil)
                .exists{ ma => hasLabels(tcArg.labels, ma) }
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

      // val firstLabel: String = testCase.labels.head
      // var subset = mentions.filter(_ matches firstLabel) {
      //   if subset.forall{hasLabels} 
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