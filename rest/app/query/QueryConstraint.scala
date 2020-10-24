package org.parsertongue.mr.rest.query

import org.clulab.odin.Mention
import org.clulab.serialization.json._
import org.clulab.odin.serialization.json._
import org.json4s._
import org.json4s.JsonAST.JValue
import org.json4s.JsonDSL._


/**
  * Represents a query constraint
  */
case class QueryConstraint(
  text: String,
  providedSubtype: Option[String] = None,
  labels: Seq[String],
  arguments: Option[Seq[Argument]]
) extends JSONSerialization with Args with Labels {

  implicit val formats = org.json4s.DefaultFormats

  def jsonAST: JValue = {
    ("subtype" -> subtype) ~
    ("labels" -> labels) ~
    ("text" -> text) ~
    ("arguments" -> argsRepresentation)
  } 

}

/**
  * Utilities for converting Odin Mentions to Query representations
  */
object QueryConstraint {
  def apply(m: Mention): QueryConstraint = {
    val args = QueryUtils.convertArgs(m.arguments)
    m match {
      case constraint if constraint matches "Constraint" =>
        QueryConstraint(
          text = m.text,
          providedSubtype = Some(m.label),
          labels = m.labels,
          arguments = args
        )
      case other =>
        println(s"QueryConstraint with label '${other.label}' not implemented")
        QueryConstraint(
          text = other.text,
          providedSubtype = Some("UnknownConstraint"),
          labels = other.labels,
          arguments = args
        )
    }
  }
}