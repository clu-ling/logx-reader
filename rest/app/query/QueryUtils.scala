package org.parsertongue.mr.rest.query

import org.clulab.odin.Mention
import org.clulab.serialization.json._
import org.clulab.odin.serialization.json._
import org.json4s._
import org.json4s.JsonAST.JValue
import org.json4s.JsonDSL._

/**
  * Utilities for transforming Query mentions.
  */
object QueryUtils {

  def toQueries(mentions: Seq[Mention]): Seq[Query] = mentions.flatMap(toQuery)

  def toQuery(mention: Mention): Option[Query] = if (! (mention matches "Query") ) {
    None
  } else {

    val metadata = QueryMetadata(foundBy = mention.foundBy, id = mention.id)
    val need: Option[QueryNeed] = mention match {
      case hasNeed if (mention.arguments contains "need") =>
        // FIXME: we're assuming there is a single need without checking
        val m = mention.arguments("need").head
        Some(QueryNeed(m))
      case _ => None
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
  def convertArgs(args: Map[String, Seq[Mention]]): Option[Seq[Argument]] = {
    val res: Seq[Argument] = args.keys.flatMap { 
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
    if (res.nonEmpty) Some(res) else None
  }
}