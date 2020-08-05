package org.parsertongue.mr.utils

import com.typesafe.scalalogging.LazyLogging
import org.clulab.processors.{ Document, Sentence }
import org.clulab.odin._
import org.clulab.odin.impl.Taxonomy
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

import java.util.Collection
import scala.io.Source

/** Utilities related to odin mentions */
object OdinUtils extends LazyLogging {

  /** Produces a lean Document containing only a single sentence */
  implicit def slim(m: Mention): Mention = m match {
    case tb: TextBoundMention =>
      tb.copy(document = slim(tb.document, tb.sentence))
    case rm: RelationMention =>
      rm.copy(
        document = slim(rm.document, rm.sentence),
        arguments = slim(rm.arguments)
      )
    case em: EventMention =>
      em.copy(
        document = slim(em.document, em.sentence),
        arguments = slim(em.arguments),
        trigger = slim(em.trigger).asInstanceOf[TextBoundMention]
      )
    // FIXME: handle CrossSentenceMention
    case _ => m

  }
  implicit def slim(args: Map[String, Seq[Mention]]): Map[String, Seq[Mention]] = args.mapValues{ mns => mns.map(slim) }
  implicit def slim(d: Document, sentenceIndex: Int): Document = {

    val s = d.sentences(sentenceIndex)
    // FIXME: first correct/zero character offsets?
    val ss = Array[Sentence](s)
    val docId: Option[String] = d.id match {
      case Some(oldId) => Some(s"$oldId-sentence-idx-${sentenceIndex}")
      case None => None
    }
    val slimDoc = Document(ss)
    slimDoc.id = docId
    slimDoc
  }


  /**
    * Finds the semantic head for a Mention.  If none is found, the rightmost word will be used by default.
    */
  def getSemHeadWord(m: Mention): String = {
    val hwo = m.semHeadWord
    if (hwo.isEmpty) {
      logger.info(s"Error finding '.semHeadWord' for '${m.text}' in sentence: '${m.sentenceObj.getSentenceText}'")
      m.words.last
    } else { hwo.get }
  }

  /***
    * Takes an Odin Mention (m) and produces a markup of the evidence span.
    * Creates "mention-arg", "mention-arg-$role" (for each argument),
    * "mention-trigger" (for each trigger),
    * "mention", and "mention-$label" (for total mention span) classes for spans
    */
  def toHtml(m: Mention): String = {

    val tag = "mark"
    val CLOSE = s"</$tag>"

    val toks: Array[String] = m.sentenceObj.words.clone()

    // markup args
    for {
      (role, arguments) <- m.arguments
      arg <- arguments
    } {
      // assign two classes
      val startIdx = arg.start
      val endIdx = arg.end - 1
      val OPEN = s"""<$tag class="mention-arg mention-arg-$role">"""
      toks(startIdx) = OPEN + toks(startIdx)
      toks(endIdx) = toks(endIdx) + CLOSE
    }

    // markup trigger (if present)
    m match {
      case em: EventMention =>
        val startIdx = em.trigger.start
        val endIdx = em.trigger.end - 1
        val OPEN = s"""<$tag class="mention-trigger">"""
        toks(startIdx) = OPEN + toks(startIdx)
        toks(endIdx) = toks(endIdx) + CLOSE
      case _ => ()
    }

    // markup total span
    toks(m.start) = s"""<$tag class="mention mention-${m.label}">""" + toks(m.start)
    toks(m.end - 1) = toks(m.end - 1) + CLOSE

    toks.mkString(" ")
  }
  
}