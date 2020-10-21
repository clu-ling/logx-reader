package org.parsertongue.mr.rest.query

import org.clulab.serialization.json.JSONSerialization
import org.json4s._
import org.json4s.JsonAST.JValue
import org.json4s.JsonDSL._


/**
  * Represents a Query
  */
case class Query(
  need: Option[QueryNeed],
  labels: Seq[String],
  constraints: Seq[QueryConstraint],
  metadata: QueryMetadata
) extends JSONSerialization {

  def jsonAST: JValue = {
    ("need" -> need.map(_.jsonAST)) ~
    ("labels" -> labels) ~
    ("constraints" -> constraints.map(_.jsonAST).toList) ~
    ("metadata" -> metadata.jsonAST)
  }

}