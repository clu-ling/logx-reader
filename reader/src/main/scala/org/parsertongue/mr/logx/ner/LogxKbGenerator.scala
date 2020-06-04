package org.parsertongue.mr.logx.ner

import ai.lum.common.FileUtils._
import ai.lum.common.ConfigUtils._
import org.parsertongue.mr.ner.{ KbEntry, KbGenerator }
import org.parsertongue.mr.logx.processors.LogxProcessor
import org.parsertongue.mr.logx.ner.LogxNerPostProcessor
import com.typesafe.config.{ Config, ConfigFactory }
import com.typesafe.scalalogging.LazyLogging
import org.clulab.processors.clu.tokenizer.Tokenizer
import org.clulab.utils.ScienceUtils
import java.io._
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

import scala.io.Source

/**
  * [[KbGenerator]] for the agriculture domain.
  * @param config Configuration file for input-output locations
  */
class LogxKbGenerator(val config: Config = ConfigFactory.load())
  extends KbGenerator with LazyLogging {

  /** Minimal processor, used solely for the tokenization of resources */
  lazy val tokenizer: Tokenizer = (new LogxProcessor).tokenizer

  /** Utility for normalizing unicode characters */
  val su = new ScienceUtils

  /** Stoplist of terms that should never match in NER */
  val stopWords: Set[String] = (new LogxNerPostProcessor).stopWords

  /**
    * Returns the input [[String]] with ascii-only characters
    */
  def normalize(text: String): String = su.replaceUnicodeWithAscii(text)
}

/**
  * Produces taxonomic dictionaries.
  */
object LogxKbGenerator extends LazyLogging {
  val config: Config = ConfigFactory.load()

  // The index of the KB names in the KB config file
  val nameField = 0
  // The index of the taxonomic labels in the KB config file
  val labelField = 1

  // Performs the actual conversion
  val generator = new LogxKbGenerator()

  /**
    * Produces taxonomic dictionaries.
    * @param args All arguments are ignored. Refer to reference.conf.
    */
  def main (args: Array[String]) {
    // Find out which KBs are to be converted and to what labels
    val kbConfig = config[String]("org.parsertongue.mr.ner.kbConfig")
    val entries = generator.loadConfig(kbConfig, nameField, labelField)

    // Find the file path of the resource directory
    val currentDir = new File(".").getCanonicalPath
    val resourceElements = Seq("reader", "src", "main", "resources")
    val resourceDir = (currentDir +: resourceElements).mkString(File.separator)

    val kbRawDir = config[String]("org.parsertongue.mr.ner.kbRawDir")
    val inDir    = resourceDir + File.separator + kbRawDir
    logger.info("Input directory: " + inDir)

    val outDir = config[String]("org.parsertongue.mr.ner.kbNerDir")
    // val outDirLoc = resourceDir + File.separator + outDir
    // logger.info(s"Output directory: ${outDir}")

    logger.info(s"Will convert a total of ${entries.size} KBs:")

    generator.writeEntries(
      entries = entries, 
      nameField = nameField, 
      inDir = inDir, 
      outDir = outDir
    )
  }
}