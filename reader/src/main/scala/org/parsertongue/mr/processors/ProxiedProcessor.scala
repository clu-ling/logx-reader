package org.parsertongue.mr.processors

import org.clulab.processors.{ Document => CluDocument, Processor, Sentence }
import org.clulab.serialization.json.JSONSerializer 

import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.prettyJson
import org.http4s._
import org.http4s.dsl.io._

import scala.concurrent.ExecutionContext.global
import cats.effect.Blocker
import java.util.concurrent._

// FIXME: implement me
class ProxiedProcessor(url: String) extends Processor {

  // FIXME: implement me
  override def annotate(text: String, keepText: Boolean): CluDocument = {

    // make httpClient
    val blockingPool = Executors.newFixedThreadPool(5)
    val blocker = Blocker.liftExecutorService(blockingPool)
    val httpClient: Client[IO] = JavaNetClientBuilder[IO](blocker).create

    // make URI for request
    val baseUri = Uri(url)
    val withPath = baseUri.withPath("/api/annotate")

    // make POST request
    val req = POST(text, withPath)
    val response: Json = httpClient.expect(req).unsafeRunSync()
    
    // Use json4s.jackson to parse json response to JValue
    val json: JValue = parse(req)
    val doc: ClueDocument = JSONSerializer.toDocument(json)

    doc
    //CluDocument(Array.empty[Sentence])
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
