package org.parsertongue.mr

import org.parsertongue.mr.MachineReadingSystem
//import org.parsertongue.mr.processor.ProxiedProcessor
import org.clulab.odin.{ Mention, TextBoundMention }
import org.clulab.processors.{ Document, Processor }
import org.clulab.serialization.json.JSONSerializer
import org.json4s.jackson.JsonMethods.parse


object TestUtils {
  // use default conf
  val system: MachineReadingSystem = MachineReadingSystem()

  //val proc: Processor = new ProxiedProcessor()

  def hasEventWithArguments(label: String, args: Seq[String], mentions: Seq[Mention]): Boolean = {
    val possible = mentions.filterNot(_.isInstanceOf[TextBoundMention]).filter(_ matches label)
    possible.exists { m =>
      val allText = m.arguments.values.flatten.map(arg => arg.text.toLowerCase).toSeq
      args.forall { arg => allText contains arg.toLowerCase}
    }
  }

}