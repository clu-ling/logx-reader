package org.parsertongue.mr.rest.query

import org.clulab.serialization.json.JSONSerialization
import org.json4s._
import org.json4s.JsonAST.JValue
import org.json4s.JsonDSL._


/**
 * serialization for arguments
 */
trait Args {

  val arguments: Option[Seq[Argument]]

  protected def argsRepresentation: JValue = arguments match {
    case Some(args) => args.map(_.jsonAST)
    case None => JNothing
  }

}