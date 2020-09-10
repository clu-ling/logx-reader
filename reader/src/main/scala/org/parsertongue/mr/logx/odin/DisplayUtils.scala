package org.parsertongue.mr.logx.odin

import org.clulab.odin._
import org.clulab.processors.{ 
  Document => CluDocument, 
  Sentence => CluSentence 
}


object DisplayUtils {

  def summarizeMentions(mentions: Seq[Mention], doc: CluDocument): String = {

    val mentionsBySentence = mentions groupBy (_.sentence) mapValues (_.sortBy(_.start)) withDefaultValue Nil

    val sentenceSummaries = for ((s, i) <- doc.sentences.zipWithIndex) yield {

      val sortedMentions = mentionsBySentence(i).sortBy(_.label)
      val (events, entities) = sortedMentions.partition(_ matches "Event")
      val (tbs, rels) = entities.partition(_.isInstanceOf[TextBoundMention])
      val sortedEntities = tbs ++ rels.sortBy(_.label)
      val entitySummaries = sortedEntities map summarizeMention
      val eventSummaries = events map summarizeMention
      val boundary = "=" * 50

      s"""
         |sentence #$i
         |TEXT:   ${s.getSentenceText}
         |TOKENS: ${(s.words.indices, s.words, s.tags.get).zipped.mkString(", ")}
         |ENTITY LABELS: ${(s.words, s.entities.get).zipped.mkString(", ")}
         |${syntacticDependenciesToString(s)}
         |ENTITIES: ${sortedEntities.size}
         |${entitySummaries.mkString("\n")}
         |EVENTS:   ${events.size}
         |${eventSummaries.mkString("\n")}
         |$boundary
       """.stripMargin
    }

    sentenceSummaries.mkString("\n")
  }

  def displayMentions(mentions: Seq[Mention]): Unit = {
    println(summarizeMentions(mentions, mentions.head.document))
  }

  def printSyntacticDependencies(s: CluSentence): Unit = {
    println(syntacticDependenciesToString(s))
  }

  def syntacticDependenciesToString(s: CluSentence): String = {

    val lemmas = s.lemmas.get.mkString(" ")

    val summaryOfDependencies = s.dependencies match {
      case Some(deps) => deps.toString
      case None => ""
    }

    s"""
       |LEMMAS: $lemmas
       |$summaryOfDependencies
     """.stripMargin
  }

  def summarizeMention(mention: Mention): String = {
    val boundary = s"\t${"-" * 30}"
    val mentionType = mention.getClass.toString.split("""\.""").last

    val summaryOfArgs = summarizeArguments(mention)

    val mentionContents = mention match {
      case em: EventMention =>
        List(
          s"$boundary",
          s"\tTRIGGER => ${em.trigger.text}",
          summaryOfArgs
        )
      case rel: RelationMention =>
        List(
          boundary,
          summaryOfArgs
        )
      case _ => Nil
    }

      s"""
        |MENTION TEXT:  ${mention.text}
        |LABELS:        ${mention.labels}
        |$boundary
        |\tRULE => ${mention.foundBy}
        |\tTYPE => $mentionType
        |$boundary
        |${mentionContents.mkString("\n").replaceFirst("\\s+$", "")}
        |$boundary
        |""".stripMargin
  }

  def displayMention(mention: Mention): Unit = {
    println(summarizeMention(mention))
  }

  def summarizeArguments(m: Mention): String = {
    val argSummaries = for {
      (k, vs) <- m.arguments
      v <- vs
    } yield s"\t$k (${v.labels}) => ${v.text}"
    argSummaries.mkString("\n")
  }

  def displayArguments(m: Mention): Unit = {
    println(summarizeArguments(m))
  }

  def cleanVerbose(s: String): String = {
    val spaceBefore = """\s+([ .,;!?%)\]}>])""".r
    val firstStep = spaceBefore replaceAllIn (s, m => m group 1)
    val spaceAfter = """([(\[{])\s+""".r
    spaceAfter replaceAllIn (firstStep, m => m group 1)
  }

}
