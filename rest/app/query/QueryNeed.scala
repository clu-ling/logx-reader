package org.parsertongue.mr.rest.query

import org.clulab.odin.Mention
import org.clulab.serialization.json.JSONSerialization
import org.json4s._
import org.json4s.JsonAST.JValue
import org.json4s.JsonDSL._


/**
  * Represents a query need
  */
case class QueryNeed(
  text: String,
  providedSubtype: Option[String] = None,
  labels: Seq[String],
  arguments: Option[Seq[Argument]]
) extends JSONSerialization with Args with Labels {

  def jsonAST: JValue = {
    ("text" -> text) ~
    ("subtype" -> subtype) ~
    ("labels" -> labels) ~
    ("arguments" -> argsRepresentation)
  }
}


object QueryNeed {
  /**
  * Convert Odin Mention to QueryNeed
  */
  def apply(m: Mention): QueryNeed = {
    val args = QueryUtils.convertArgs(m.arguments)
    QueryNeed(
      text = m.text,
      providedSubtype = Some(m.label),
      labels = m.labels,
      arguments = args
    )
  }
}