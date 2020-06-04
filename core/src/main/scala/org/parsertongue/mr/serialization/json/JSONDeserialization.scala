package org.parsertongue.mr.serialization.json

import java.io.File

import ai.lum.common.FileUtils._
import org.json4s.JsonAST.JValue
import org.json4s.jackson.JsonMethods.parse

/** Deserialization methods for handling JSON */
trait JSONDeserialization[T] {

  def fromJson(json: JValue): T

  def fromFile(file: File): T = {
    val contents: String = file.readString()
    val json = parse(contents, useBigDecimalForDouble = false)
    fromJson(json)
  }

}