package org.parsertongue.mr.rest.query

import org.clulab.serialization.json.JSONSerialization
import org.json4s._
import org.json4s.JsonAST.JValue
import org.json4s.JsonDSL._

/**
  * Represents a mention's argument
  */
case class Argument(
  role: String,
  text: String,
  providedSubtype: Option[String] = None,
  labels: Seq[String]
) extends JSONSerialization with Labels { 

  def jsonAST: JValue = {
    ("role" -> role) ~
    ("text" -> text) ~
    ("subtype" -> subtype) ~
    ("labels" -> labels)
  }
}