package org.parsertongue.mr.rest

import org.clulab.odin.Mention
import org.clulab.serialization.json._
import org.clulab.odin.serialization.json._
import org.json4s._
import org.json4s.JsonAST.JValue
import org.json4s.JsonDSL._

package object query {

  implicit val formats = org.json4s.DefaultFormats

  /**
  * Wraps a Seq[Query]
  */
  implicit class QueryOps(
    queries: Seq[Query]
  ) extends JSONSerialization {
    def jsonAST: JValue = JArray(queries.distinct.map(_.jsonAST).toList)
  }
  
}