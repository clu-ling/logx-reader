package org.parsertongue.mr.ner

import ai.lum.common.FileUtils._

import com.typesafe.scalalogging.LazyLogging
import org.parsertongue.mr.ner.KbEntry
import com.typesafe.config.{ Config, ConfigFactory }
//import com.typesafe.scalalogging.LazyLogging
import org.clulab.processors.clu.tokenizer.Tokenizer

import scala.collection.JavaConverters._
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.{ Constructor, ConstructorException }

import java.io._
import java.text.SimpleDateFormat
import java.util.Date

        // val yaml = new Yaml
        // val obj = yaml.load(data)
    // val yaml = new Yaml(new Constructor(classOf[Collection[JMap[String, Array[String]]]]))
    // val jRules = yaml.load(input).asInstanceOf[Collection[JMap[String, Array[String]]]].asScala
    
/**
  * Parent class for classes that produce taxonomically defined dictionaries from knowledge bases.
  */
trait KbGenerator extends LazyLogging { 

  val tokenizer: Tokenizer

  val stopWords: Set[String]

  def normalize(text: String): String

  /**
    * Tokenizes a resource line. </br>
    * It is important to guarantee that KB text is processed similarly to raw text!
    * @param line The KB line
    * @return The tokenized line
    */
  def tokenizeResourceLine(line: String): Array[String] = {
    tokenizer
      .tokenize(line, sentenceSplit = false) // Array[Sentence]
      .headOption                            // Option[Sentence]
      .map(_.words)                          // Option[Array[String]]
      .getOrElse(Array[String]())            // Array[String]
  }

  /**
  * Returns a file location for the input to a KB -> dictionary conversion
  * @param entry The KbEntry being converted
  * @param inputDir The location of the input kb
  */
  def mkInputFile(entry: KbEntry, inputDir: String): File = {
    val base = inputDir + File.separator + entry.kbName + ".tsv"
    // FIXME: this is dirty
    if (new File(base).exists()) new File(base) else new File(base + ".gz")
  }


  /**
    * Returns a file location for the output of a KB -> dictionary conversion
    * @param entry The KbEntry being converted
    * @param outputDir The location for the output dictionaries
    */
  def mkOutputFile(entry: KbEntry, outputDir: String): File = {
    new File(outputDir + File.separator + entry.neLabel + ".tsv.gz")
  }

  /**
    * Returns tokenized dictionary terms from a tab-separated knowledge base.
    * @param entry The KB to read from and what taxonomic label it corresponds to
    * @param inputDir The location of the KB
    */
  def convertKB(entry: KbEntry, inputDir: String, nameField: Int): Seq[String] = {
    // load KB file
    val inFile = mkInputFile(entry, inputDir)
    val lines = inFile.readString().split("\n")
    val outputLines = lines flatMap { line =>
      val trimmedLine = line.trim
      if(! trimmedLine.isEmpty && ! trimmedLine.startsWith("#")) { // ignore comments/blank
        // select
        val kbTokens = line.split("\t")
        val term = kbTokens(nameField)
        // normalize unicode characters
        val normalizedTerm = normalize(term)
        val tokens = tokenizeResourceLine(normalizedTerm)
        val tokenized = tokens.mkString(" ")
        if (stopWords.contains(tokenized.toLowerCase)) {
          None
        } else {
          Option(tokenized)
        }
      } else { // #-comment or blank
        None
      }
    }

    val kbRows = outputLines
      .filter(_.nonEmpty) // in case some lines contain just \t
      .toList // Iterator -> List
      .sorted // easier debug and trie and .distinct
      .distinct // .distinct benefits from .sorted

    kbRows
  }

  def writeEntries(
    entries: Seq[KbEntry], 
    nameField: Int, 
    inDir: String,
    outDir: String
  ): Unit = {
    
    for(entry <- entries) {
      val previousOutput = mkOutputFile(entry, outDir)
      // delete the previous output
      if(previousOutput.exists()) {
        previousOutput.delete()
        logger.info(s"Deleted old output ${previousOutput.getAbsolutePath}.")
      }
    }

    for(entry <- entries) {
      logger.info(s"KB:${entry.kbName} to NE:${entry.neLabel}.")
      val outFile = mkOutputFile(entry, outDir)
      //.writeString(contents, gzipSupport = true, append = true)
      // get the KB's dictionary entries (tokenized)
      val lines = convertKB(entry, inDir, nameField)

      // append to output; we may have multiple KBs using the same taxonomic label
      val isFirst = ! outFile.exists()
      if(isFirst) {
        outFile.writeString(s"# Created by ${getClass.getName} on $now.", gzipSupport = true, append = true)
      }

      outFile.writeString(lines.mkString("\n"), gzipSupport = true, append = true)

      logger.info(s"Done. Read ${lines.length} lines from ${entry.kbName}")
    }
  }

  /**
    * Returns [[KbEntry]]s to be loaded from a tab-separated configuration file
    * @param configFile The location of the configuration file
    * @param nameField The index of KB name in configFile
    * @param labelField The index of the taxonomic label in configFile
  */
  def loadConfig(configFile: String, nameField: Int, labelField: Int): Seq[KbEntry] = {
    println(configFile)
    // load KB config file
    // val lines = Source.fromResource(configFile).getLines() map (_.trim) // Scala 2.12 and later
    val javastyle = File.separator + configFile
    val stream: InputStream = getClass.getResourceAsStream(javastyle)
    val lines = scala.io.Source.fromInputStream(stream).getLines map (_.trim)
    // ignore blank lines and #-comments
    val contentLines = lines filterNot (line => line.isEmpty || line.startsWith("#"))

    val kbEntries = contentLines map { line: String =>
      // each line must have at least 2 tab-separated values
      val tokens = line.split("\t")
      assert(tokens.length >= 2, line)
      val kbName = tokens(nameField)
      val neLabel = tokens(labelField)
      KbEntry(kbName, neLabel)
    }

    kbEntries.toSeq
  }

  /**
    * Returns a [[String]] representation of the current date and time when the function is called.
    */
  def now: String = {
    val dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
    val date = new Date()
    dateFormat.format(date)
  }
}