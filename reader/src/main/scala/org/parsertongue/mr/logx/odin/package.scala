package org.parsertongue.mr.logx

import java.util.Collection

import scala.io.Source

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

import org.clulab.odin.impl.Taxonomy
import org.clulab.odin._
import org.clulab.processors.Document

/**
  * Utilities related to LogX reader
  */
package object odin {

  // Taxonomy object
  val taxonomy = readTaxonomy("org/parsertongue/reader/grammars/logx/taxonomy.yml")

  private def readTaxonomy(path: String): Taxonomy = {
    val url = getClass.getClassLoader.getResource(path)
    val source = if (url == null) Source.fromFile(path) else Source.fromURL(url)
    val input = source.mkString
    source.close()
    val yaml = new Yaml(new Constructor(classOf[Collection[Any]]))
    val data = yaml.load(input).asInstanceOf[Collection[Any]]
    Taxonomy(data)
  }

  implicit class CrossSentenceMentionOps(cm: CrossSentenceMention) {
    def copy(
      labels: Seq[String] = cm.labels,
      anchor: Mention = cm.anchor,
      neighbor: Mention = cm.neighbor,
      arguments: Map[String, Seq[Mention]] = cm.arguments,
      document: Document = cm.document,
      keep: Boolean = cm.keep,
      foundBy: String = cm.foundBy,
      attachments: Set[Attachment] = cm.attachments
    ): CrossSentenceMention = {
      new CrossSentenceMention(
        labels = labels,
        anchor = anchor,
        neighbor = neighbor,
        arguments = arguments,
        document = document,
        keep = keep,
        foundBy = foundBy,
        attachments = attachments
      )
    }
  }
}
