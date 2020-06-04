package org.parsertongue.mr.utils

import org.clulab.openie.ResourceUtils

import scala.io.Source
import scala.util.matching.Regex

object RuleUtils {

  private val URL_PATTERN = new Regex("""(?i)^(http|ftp|localhost|file).+""")

  def read(rulesPath: String): String = rulesPath match {

    // FIXME: make this prefix-aware (URL vs filesystem vs resource)
    case url if URL_PATTERN.pattern.matcher(url).matches =>
      Source.fromURL(url).mkString

    // FIXME: will this work for file:// ?
    case resource =>
      val stream = getClass.getClassLoader.getResourceAsStream(rulesPath)
      val source = scala.io.Source.fromInputStream(stream)
      val data = source.mkString
      source.close()
      data

  }
}
