package org.parsertongue.mr.rest.query

import org.clulab.serialization.json.JSONSerialization
import org.json4s._
import org.json4s.JsonAST.JValue
import org.json4s.JsonDSL._


/**
  * Metadata associated with a query
  */
case class QueryMetadata(
  foundBy: String,
  id: String
) extends JSONSerialization {

  def jsonAST: JValue = {
    ("foundBy" -> foundBy) ~
    ("id" -> id)
  }
}