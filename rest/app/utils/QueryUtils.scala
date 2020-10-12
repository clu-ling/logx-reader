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
    * Utilities for transforming Query mentions.
    */
  object QueryUtils {

    def toQueries(mentions: Seq[Mention]): Seq[Query] = mentions.flatMap(toQuery)

    def toQuery(mention: Mention): Option[Query] = if (! (mention matches "Query") ) {
      None
    } else {

      val metadata = QueryMetadata(foundBy = mention.foundBy, id = mention.id)
      // FIXME: this isn't safe...
      val need: QueryNeed = {
        val m = mention.arguments("need").head
        QueryNeed(
          text   = m.text,
          labels = m.labels
        )
      }

      val constraints: Seq[QueryConstraint] = mention.arguments.getOrElse("constraints", Nil).map(QueryConstraint.apply).distinct

      Some(
        Query(
          need = need,
          labels = mention.labels,
          constraints = constraints,
          metadata = metadata 
        )
      )
    }

    /**
      * Converts mentions args to a simplified representation.  Lossy process.
      */
    def convertArgs(args: Map[String, Seq[Mention]]): Seq[Argument] = args.keys.flatMap { 
      case arg => 
        val m = args(arg).head
        Seq(
          Argument(
            role = arg,
            text = m.text,
            labels = m.labels
          )
        )
    }.toSeq.distinct

  }

  /**
    * Represents a Query
    */
  case class Query(
    need: QueryNeed,
    labels: Seq[String],
    constraints: Seq[QueryConstraint],
    metadata: QueryMetadata
  ) extends JSONSerialization {

    def jsonAST: JValue = {
      ("need" -> need.jsonAST) ~
      ("labels" -> labels) ~
      ("constraints" -> constraints.map(_.jsonAST).toList) ~
      ("metadata" -> metadata.jsonAST)
    }

  }

  /**
    * Represents a mention's argument
    */
  case class Argument(
    role: String,
    text: String,
    labels: Seq[String]
  ) extends JSONSerialization { 

    def jsonAST: JValue = {
      ("role" -> role) ~
      ("text" -> text) ~
      ("labels" -> labels)
    }
  }

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


  /**
    * Represents a query need
    */
  case class QueryNeed(
    text: String,
    labels: Seq[String]
  ) extends JSONSerialization {

    def jsonAST: JValue = {
      ("text" -> text) ~
      ("labels" -> labels)
    }
  }

  /**
    * Represents a query constraint
    */
  case class QueryConstraint(
    subtype: String,
    arguments: Seq[Argument]
  ) extends JSONSerialization {
    def jsonAST: JValue = {
      ("subtype" -> subtype) ~
      ("arguments" -> arguments.map(_.jsonAST))
    } 
  }


  /**
    * Wraps a Seq[Query]
    */
  implicit class QueryOps(
    queries: Seq[Query]
  ) extends JSONSerialization {
    def jsonAST: JValue = JArray(queries.distinct.map(_.jsonAST).toList)
  }

  /**
    * Utilities for converting Odin Mentions to Query representations
    */
  object QueryConstraint {
    def apply(m: Mention): QueryConstraint = {
      val args = QueryUtils.convertArgs(m.arguments)
      m match {
        case proximity if proximity matches "ProximityConstraint" =>
          QueryConstraint(
            subtype = "ProximityConstraint",
            arguments = args
          )
        case quantity if quantity matches "QuantityConstraint" =>
          QueryConstraint(
            subtype = "QuantityConstraint",
            arguments = args
          )
        case quantity if quantity matches "TimeConstraint" =>
          QueryConstraint(
            subtype = "TimeConstraint",
            arguments = args
          )
        case other =>
          println(s"QueryConstraint with label '${other.label}' not implemented")
          QueryConstraint(
            subtype = "UnknownConstraint",
            arguments = args
          )
      }
    }
  }

}