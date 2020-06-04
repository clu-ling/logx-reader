package org.parsertongue.mr

import org.parsertongue.mr.serialization.json.JSONDeserialization
import org.clulab.serialization.json.JSONSerialization
import org.json4s.JsonAST.JValue
import org.json4s.JsonDSL._
import org.json4s.jackson.Serialization
import org.json4s.{ DefaultFormats, Formats, ShortTypeHints, _ }


case class Document(
  metadata: DocumentMetadata,
  text: String,
) extends JSONSerialization {

  import Document._
  // implicit val formats: Formats = DefaultFormats

  // implicit val formats: Formats = Document.formats

  def jsonAST: JValue = Extraction.decompose(this)

}

object Document extends JSONDeserialization[Document] {

  //implicit val formats: Formats = DefaultFormats

  implicit val formats: Formats = Document.formats
  
  def fromJson(json: JValue): Document = json.extract[Document]

}