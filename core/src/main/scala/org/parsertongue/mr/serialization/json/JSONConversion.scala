package org.parsertongue.mr.serialization.json

import java.io.{ File, InputStream }

import ai.lum.common.FileUtils._
import org.json4s.JsonAST.JValue

/** Skeleton for any implementation that converts documents (xml, pdf, etc.)
  * into a structured representation encoded as json.
  */
trait JSONConversion {

  def toJson(is: InputStream): JValue

  def toJson(file: File): JValue = {
    val is = file.toInputStream
    toJson(is)
  }
}