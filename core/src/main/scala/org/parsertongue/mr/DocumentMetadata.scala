package org.parsertongue.mr

import java.net.{ URL, URLEncoder }
import java.nio.charset.StandardCharsets

import  org.parsertongue.mr.serialization.json.JSONDeserialization
import org.clulab.serialization.json.JSONSerialization
import org.json4s.jackson.Serialization
import org.json4s.{ Formats, ShortTypeHints, _ }

/** Metadata of interest that is associated with a paper */
case class DocumentMetadata(
  docId: Option[String],
  section: Option[String],
  title: Option[String],
  publicationDate: Option[DateTime]
) extends JSONSerialization {

  import DocumentMetadata._

  override def jsonAST: JValue = {
    //implicit val formats: Formats = DocumentMetadata.formats
    Extraction.decompose(this)
  }
}

object DocumentMetadata extends JSONDeserialization[DocumentMetadata] {

  //implicit val formats: Formats = Serialization.formats(ShortTypeHints(List(classOf[DOI], classOf[DocUrl])))

  implicit val formats: Formats = DocumentMetadata.formats
  
  def fromJson(json: JValue): DocumentMetadata = {
    //implicit val formats: Formats = DocumentMetadata.formats
    json.extract[DocumentMetadata]
  }

  def empty = DocumentMetadata(docId = None, section = None, title = None, publicationDate = None)
}

case class DateTime(year: Option[Int], month: Option[Int], day: Option[Int])

// /** a kind of document id */
// trait DocumentIdentifier {

// }

// trait Url {
//   def url: String
// }

// /** Digital Object Identifier */
// case class DOI(doi: String) extends DocumentIdentifier with Url {

//   override def url: Option[String] = new URL(s"https://doi.org/${URLEncoder.encode(doi, StandardCharsets.UTF_8.toString)}").toString

// }

// case class DocUrl(url: String) extends DocumentIdentifier with Url {

//   override def url: Option[String] = new URL(URLEncoder.encode(url, StandardCharsets.UTF_8.toString)).toString

// }

// /** Document author */
// case class Author(givenName: String, surName: String, email: Option[String], affiliation: Option[Organization])

// /** Organization (ex. author's affiliation) */
// case class Organization(organization: String, department: Option[String])
