package org.parsertongue.mr.serialization.json

import org.parsertongue.mr.serialization._
import org.parsertongue.mr.Document
import org.json4s.JsonAST.JValue
import org.json4s.jackson.Serialization
import org.json4s.{ DefaultFormats, Formats, ShortTypeHints, _ }

/** Support for deserializing json to a [[org.parsertongue.mr.Document]] */
trait DocConversion {

  // implicit val formats: Formats = Serialization.formats(ShortTypeHints(List(classOf[DOI], classOf[Concept], classOf[Subject], classOf[Author])))

  implicit val formats: Formats = DefaultFormats
  
  def toDocument(json: JValue): Document = json.extract[Document]

}