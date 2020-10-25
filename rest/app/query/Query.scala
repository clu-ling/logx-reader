package org.parsertongue.mr.rest.query

import org.clulab.serialization.json.JSONSerialization
import org.json4s._
import org.json4s.JsonAST.JValue
import org.json4s.JsonDSL._


/**
  * Represents a Query
  */
case class Query(
  need: QueryNeed,
  providedSubtype: Option[String] = None,
  labels: Seq[String],
  constraints: Seq[QueryConstraint],
  metadata: QueryMetadata
) extends JSONSerialization with Labels {

  val constraintsJdata: JValue = constraints match {
    case Nil => JNothing
    case _   => constraints.map(_.jsonAST).toList
  }

  def jsonAST: JValue = {
    ("need" -> need.jsonAST) ~
    ("subtype" -> subtype) ~
    ("labels" -> labels) ~
    ("constraints" -> constraintsJdata) ~
    ("metadata" -> metadata.jsonAST)
  }

}