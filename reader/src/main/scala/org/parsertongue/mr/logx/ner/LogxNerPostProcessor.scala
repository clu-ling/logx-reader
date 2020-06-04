package org.parsertongue.mr.logx.ner

import org.clulab.processors.clu.SentencePostProcessor
import org.clulab.processors.Sentence

import com.typesafe.config.{ Config, ConfigFactory }

class LogxNerPostProcessor(config: Config = ConfigFactory.load("reference")) extends SentencePostProcessor {
  
  //val stopListFile: Option[String] = config.get[String]("org.parsertongue.mr.ner.stopListFile")
  val stopWords: Set[String] = Set.empty[String]
  //val stopWords: Set[String] = loadEntityStopList(stopListFile)


  override def process(sent: Sentence): Unit = {
    //val seq = sent.entities.get

    // // find B I* sequences and set them to O if invalid
    // var i = 0
    // while(i < seq.length) {
    //   if(isEntityStart(i, seq)) {
    //     val end = findEntityEnd(i, seq)
    //     if(! validMatch(sent, i, end)) {
    //       for(j <- i until end) {
    //         // if not valid, reset the entire span labels to O
    //         seq(j) = LexiconNER.OUTSIDE_LABEL
    //       }
    //     }
    //     i = end
    //   } else {
    //     i += 1
    //   }
    // }
  }

}