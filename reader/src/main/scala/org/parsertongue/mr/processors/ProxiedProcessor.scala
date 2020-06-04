package org.parsertongue.mr.processors

import org.clulab.processors.{ Document => CluDocument, Processor, Sentence }

// FIXME: implement me
class ProxiedProcessor(url: String) extends Processor {

  // FIXME: implement me
  override def annotate(text: String, keepText: Boolean): CluDocument = {
    CluDocument(Array.empty[Sentence])
  }

  // FIXME: implement me
  override def annotate(doc: CluDocument): CluDocument = {
    doc
  }

  def chunking(doc: CluDocument): Unit = None
  def discourse(doc: CluDocument): Unit = None
  def srl(doc: CluDocument): Unit = None
  def lemmatize(doc: CluDocument): Unit = None
  def mkDocument(text: String,keepText: Boolean): CluDocument = CluDocument(Array.empty[Sentence])
  def mkDocumentFromSentences(sentences: Iterable[String],keepText: Boolean,charactersBetweenSentences: Int): CluDocument = CluDocument(Array.empty[Sentence])
  def mkDocumentFromTokens(sentences: Iterable[Iterable[String]],keepText: Boolean,charactersBetweenSentences: Int,charactersBetweenTokens: Int): CluDocument = CluDocument(Array.empty[Sentence])
  def parse(doc: CluDocument): Unit = None
  def recognizeNamedEntities(doc: CluDocument): Unit = None
  def relationExtraction(doc: CluDocument): Unit = None
  def resolveCoreference(doc: CluDocument): Unit = None
  def tagPartsOfSpeech(doc: CluDocument): Unit = None
}
