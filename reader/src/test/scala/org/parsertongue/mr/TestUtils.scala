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

  case class EventTestCase(labels: Seq[String], text: String, args: List[ArgTestCase])

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
      println(s"testCase failed for '${testCase.text}'")
      val firstLabel: String = testCase.labels.head
      val subset = mentions.filter(_ matches firstLabel)
      if (subset.nonEmpty) { DisplayUtils.displayMentions(subset) }
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
      // check if labels found
      hasLabels(testCase.labels, m) &&
      testCase.args.forall{ tcArg => hasArg(tcArg, m) }
    }

    if (! success) {
      println(s"testCase failed for '${testCase.text}'")
      val firstLabel: String = testCase.labels.head
      val subset = mentions.filter(_ matches firstLabel)
      if (subset.nonEmpty) { 
        DisplayUtils.displayMentions(subset) 
      } else {
        DisplayUtils.displayMentions(mentions)
      }
    }
    success
  }

}