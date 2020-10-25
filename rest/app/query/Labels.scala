package org.parsertongue.mr.rest.query


/**
 * labels and subtype behavior
 */
trait Labels {

  val providedSubtype: Option[String]
  val labels: Seq[String]

  val subtype: String = providedSubtype match {
    case Some(lbl) => lbl
    case None => labels.head
  }

}