package org.parsertongue.mr.logx.processors

import org.parsertongue.mr.logx.ner.LogxKbLoader
import com.typesafe.config.{ Config, ConfigFactory }
import com.typesafe.scalalogging.LazyLogging
import org.clulab.dynet
import org.clulab.processors.Document
import org.clulab.processors.clu.{ CluProcessor, SentencePostProcessor }
import org.clulab.processors.fastnlp.FastNLPProcessor
import org.clulab.processors.shallownlp.ShallowNLPProcessor
//import org.clulab.processors.clu.sequences.EnglishPOSPostProcessor
import org.clulab.sequences.Tagger
import org.clulab.struct.{ DirectedGraph, Edge, GraphMap }
import org.clulab.utils.Configured
import org.parsertongue.mr.logx.ner.LogxNerPostProcessor

import scala.util.{ Failure, Success, Try }


/**
  * Custom [[org.clulab.processors.Processor]] for LogX.
  */
class LogxProcessor(
  val config: Config = ConfigFactory.load("logxprocessor")
) extends FastNLPProcessor(
  tokenizerPostProcessor = None,
  internStrings = false,
  withChunks = false,
  withRelationExtraction = false,
  withDiscourse = ShallowNLPProcessor.NO_DISCOURSE
) with LazyLogging 
  with Configured {
  val prefix = "processor"

  // FIXME: move this memory allocation to the conf
  dynet.Utils.initializeDyNet(autoBatch = false, mem = "2048,2048,2048,2048")

  override def getConf: Config = config

  val PREDICATE_ATTACHMENT_NAME = "predicates"
  
  // one of the multi-task learning (MTL) models, which covers: NER
  lazy val mtlNer: dynet.Metal = { 
    dynet.Metal(getArgString(s"$prefix.ner.mtl-ner", None))
  }

  // one of the multi-task learning (MTL) models, which covers: POS, chunking, and SRL (predicates)
  lazy val mtlPosChunkSrlp: dynet.Metal = { 
    dynet.Metal(getArgString(s"$prefix.mtl-pos-chunk-srlp", None))
  }

  // one of the multi-task learning (MTL) models, which covers: SRL (arguments)
  lazy val mtlSrla: dynet.Metal = { 
    dynet.Metal(getArgString(s"$prefix.mtl-srla", None))
  }

  /** NER; modifies the document in place */
  override def recognizeNamedEntities(doc: Document) {
    basicSanityCheck(doc)
    for(sent <- doc.sentences) {
      val allLabels = mtlNer.predictJointly(new dynet.AnnotatedSentence(sent.words))
      sent.entities = Some(allLabels(0).toArray)
    }
  }

  override def srl(doc: Document): Unit = {

    // generate SRL frames for each predicate in each sentence
    for (sentence <- doc.sentences) {

      val (tags, predicates): (Array[String], Array[Int]) = {
        // FIXME: find out about taskId, and switch to predict()
        val allLabels = mtlPosChunkSrlp.predictJointly(new dynet.AnnotatedSentence(sentence.words))
        val posTags = allLabels(0).toArray
        val preds: Array[Int] = allLabels(2).filter{ case lbl => lbl == "B-P" }.indices.map(_ + 1).toArray

        (posTags, preds)
      }
      //val tags = sentence.tags.get

      // SRL needs POS tags and NEs!
      // FIXME: are these entities compatible?
      val annotatedSentence =
        new dynet.AnnotatedSentence(sentence.words,
          Some(tags), Some(sentence.entities.get))

      // all predicates become roots
      val roots = predicates

      val argLabels = for (pred <- predicates) {
        mtlSrla.predict(0, annotatedSentence, Some(pred))
      }

      val edges: Seq[Edge[String]] = for {
        pred <- predicates
        argLabels = mtlSrla.predict(0, annotatedSentence, Some(pred))
        ai <- argLabels.indices
        argLabel = argLabels(ai)
        // FIXME: is this check too aggressive?
        if argLabel != "O"
      } yield Edge[String](pred, ai, argLabel)

      val srlGraph = new DirectedGraph[String](edges.toList, roots.toSet)
      sentence.graphs += GraphMap.SEMANTIC_ROLES -> srlGraph
    }
  }

  /** Lematization; modifies the document in place */
  // override def lemmatize(doc: Document) {
  //   basicSanityCheck(doc)
  //   for(sent <- doc.sentences) {
  //     val lemmata = new Array[String](sent.size)
  //     for(i <- sent.words.indices) {
  //       val originalWord = sent.words(i).toLowerCase
  //       // rare case of missing word
  //       if (originalWord.trim.isEmpty) {
  //         sent.words(i) = s"MISSING-WORD"
  //         lemmata(i)     = "MISSING-WORD"
  //       } else {
  //         // Avoid issue where the lemmatizer sometimes produces an empty string
  //         lemmata(i) = {
  //           val word = sent.words(i).toLowerCase
  //           val res = lemmatizer.lemmatizeWord(word).trim
  //           if (res.isEmpty) { word } else { res }
  //         }
  //       }
  //       assert(lemmata(i).nonEmpty)
  //     }
  //     sent.lemmas = Some(lemmata)
  //   }
  // }
}
// class LogxProcessor
//   extends CluProcessor(config = ConfigFactory.load("logxprocessor")) {
//   val prefix = "processor"

//   lazy val nerPostProcessor: Option[SentencePostProcessor] = {
//     getArgString(s"$prefix.ner.type", Some("none")) match {
//       case "logx" => Option(new LogxNerPostProcessor)
//       case "none" => None
//       case _ => throw new RuntimeException(s"ERROR: Unknown argument value for $prefix.ner.type!")
//     }
//   }

//   lazy val ner: Option[Tagger[String]] = {
//     getArgString(s"$prefix.ner.type", Some("none")) match {
//       case "logx" => Option(new LogxKbLoader().loadAll()) // FIXME: load serialized model if available
//       case "none" => None
//       case _ => throw new RuntimeException(s"ERROR: Unknown argument value for $prefix.ner.type!")
//     }
//   }

//   override lazy val mtlNer = LstmCrfMtl(getArgString(s"$prefix.ner.mtl-ner", Some("mtl-en-ner")))

//   /** NER; modifies the document in place */
//   override def recognizeNamedEntities(doc: Document) {
//     basicSanityCheck(doc)
//     for (sentence <- doc.sentences) {
//       val allLabels = mtlNer.predictJointly(sentence.words)
//       sentence.entities = Some(allLabels(0))
//     }
//     // if (ner.nonEmpty) {
//     //   basicSanityCheck(doc)
//     //   for (sentence <- doc.sentences) {
//     //     val allLabels = mtlNer.predictJointly(sentence.words)
//     //     sentence.entities = Some(allLabels(0))
//         // FIXME: merge labels
//         // see https://github.com/clulab/reach/blob/677da1eaaff0ace996ed08537f930b4ce2c49bcb/processors/src/main/scala/org/clulab/processors/bionlp/ner/HybridNER.scala#L21 for a possible solution 
//         // val labels = ner.get.find(sentence)
//         // sentence.entities = Some(labels)

//         // if(nerPostProcessor.nonEmpty) {
//         //   nerPostProcessor.get.process(sentence)
//         // }
//     //   }
//     // }
//   }

//   /** Lematization; modifies the document in place */
//   override def lemmatize(doc: Document) {
//     basicSanityCheck(doc)
//     for(sent <- doc.sentences) {
//       val lemmata = new Array[String](sent.size)
//       for(i <- sent.words.indices) {
//         val originalWord = sent.words(i).toLowerCase
//         // rare case of missing word
//         if (originalWord.trim.isEmpty) {
//           sent.words(i) = s"MISSING-WORD"
//           lemmata(i)     = "MISSING-WORD"
//         } else {
//           // Avoid issue where the lemmatizer sometimes produces an empty string
//           lemmata(i) = {
//             val word = sent.words(i).toLowerCase
//             val res = lemmatizer.lemmatizeWord(word).trim
//             if (res.isEmpty) { word } else { res }
//           }
//         }
//         assert(lemmata(i).nonEmpty)
//       }
//       sent.lemmas = Some(lemmata)
//     }
//   }

// }