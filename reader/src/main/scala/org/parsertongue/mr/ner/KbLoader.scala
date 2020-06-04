package org.parsertongue.mr.ner

import com.typesafe.scalalogging.LazyLogging

import org.clulab.sequences.{ LexiconNER, NoLexicalVariations }
import org.clulab.struct.TrueEntityValidator

import java.io._
import java.util.MissingResourceException
import java.util.zip.{ GZIPInputStream, GZIPOutputStream }


/**
  * Parent class for classes that load taxonomically defined dictionaries based on tokenized
  * knowledge bases (KBs)
  */
trait KbLoader extends LazyLogging {

  // val nerModel: Option[String]
  // val kbs: Seq[String]
  // val overrides: Option[Seq[String]]
  val stopListFile: Option[String]

  /** Create a default [[LexiconNER]] */
  def nerFromKbs(): LexiconNER 
  
  /** The file location of the resources directory */
  lazy val resourceDir: String = {
    // Find the resource path's full file path
    val currentDir = new File(".").getCanonicalPath
    val resourceElements = Seq("reader", "src", "main", "resources")
    (currentDir +: resourceElements).mkString(File.separator)
  }

  /**
    * Serializes a [[LexiconNER]] to a given resource path
    * @param ner The NER to serialize
    * @param modelPath Resource path to export to
    */
  def serializeNer(ner: LexiconNER, modelPath: String): Unit = {
    val modelFile = resourceDir + File.separator + modelPath

    val stream = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(modelFile)))
    stream.writeObject(ner)
    stream.flush()
    stream.close()
  }

  /**
    * Deserializes a [[LexiconNER]] from a given resource path
    * @param modelPath Resource path to import from
    */
  def deserializeNer(modelPath: String): LexiconNER = {
    /*val inDirLoc = resourceDir + File.separator + modelPath
    val stream = if (modelPath.endsWith(".gz")) {
      new ObjectInputStream(new GZIPInputStream(new FileInputStream(inDirLoc)))
    } else {
      new ObjectInputStream(new FileInputStream(inDirLoc))
    }
    val lexiconNer = stream.readObject().asInstanceOf[LexiconNER]
    stream.close()
    val labels = lexiconNer.matchers.map(_._1).sorted
    logger.debug(s"Loaded tries for ${labels.length} labels: ${labels.mkString(", ")}")
    lexiconNer */

    // FIXME: Deserialization of LexiconNER
    logger.warn("Deserialization is not available yet!")
    nerFromKbs()
  }

  /**
    * Returns a [[LexiconNER]], either from a previously computed serialized model, or from the
    * dictionaries themselves, if necessary.
    * @param fromModel True if loading from a serialized model
    * @param serNerLoc The resource path of the serialized model
    */
  def loadAll(
      fromModel: Boolean = false,
      serNerLoc: Option[String] = None): LexiconNER = {
    (fromModel, serNerLoc) match {
      case (false, _) => nerFromKbs() // ignore serialized models
      case (true, Some(modelPath)) => deserializeNer(modelPath) // provide serialized model
      case _ => // no default available
        throw new MissingResourceException(
          "No existing serialized model is available!",
          "LexicalNER",
          "org.parsertongue.mr.ner.model"
        )
    }
  }
}