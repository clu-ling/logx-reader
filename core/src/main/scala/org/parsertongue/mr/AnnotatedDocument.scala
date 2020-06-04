package org.parsertongue.mr

import org.parsertongue.mr.serialization.json.JSONDeserialization
import org.clulab.processors.{ Document => CluDocument }
import org.clulab.serialization.json.{ DocOps, JSONSerialization, JSONSerializer }
import org.json4s
import org.json4s.JsonAST.JValue
import org.json4s.JsonDSL._
import org.json4s._

/** [[StructuredDocument]] that has been Parsed/annotated */
case class AnnotatedDocument(
  document: Document,
  annotatedDocument: CluDocument
) extends JSONSerialization {

  def jsonAST: json4s.JValue = {
    ("document" -> document.jsonAST) ~
    ("annotatedDocument" -> annotatedDocument.jsonAST)
  }
}

object AnnotatedDocument extends JSONDeserialization[AnnotatedDocument] {

  def fromJson(json: JValue): AnnotatedDocument = {
    val doc = Document.fromJson(json \ "document")
    val odinDoc = JSONSerializer.toDocument(json \ "annotatedDocument")
    AnnotatedDocument(
      document = doc,
      annotatedDocument = odinDoc
    )
  }

}