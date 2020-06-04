package org.parsertongue.mr.actions

import org.clulab.odin._

class OdinActions extends Actions {

  def cartesianProduct(mention: Mention): Seq[Mention] = {
    //println(s"${mention.arguments.map{ case (nm, args) => s"$nm: ${args.map(_.text).mkString(", ")}"}.mkString("; ")}\n")

    val arguments = mention.arguments
    // sanity checks
    if (arguments.values.flatten.toSeq.length < 3) return Seq(mention)
    if (! arguments.contains("cause") || ! arguments.contains("effect")) return Seq(mention)
    for {
      cause <- arguments("cause")
      effect <- arguments("effect")
    } yield {
      //println(s"\tcause: ${cause.text}, effect: ${effect.text}")
      val ceArgs = Map("cause" -> Seq(cause), "effect" -> Seq(effect))
      val minArgs = (arguments - "cause" - "effect") ++ ceArgs
      mention match {
        case em: EventMention =>
          val interval = mkTokenInterval(trigger = em.trigger, arguments = minArgs)
          em.copy(tokenInterval = interval, arguments = minArgs)
        case rm: RelationMention =>
          val interval = mkTokenInterval(arguments = minArgs)
          rm.copy(tokenInterval = interval, arguments = minArgs)
      }
    }
  }

  def splitEvents(mentions: Seq[Mention], state: State): Seq[Mention] = {
    mentions flatMap {
      case tbm: TextBoundMention => Seq(tbm)
      case em: EventMention => cartesianProduct(em)
      case rm: RelationMention => cartesianProduct(rm)
    }
  }

  def identityAction(mentions: Seq[Mention], state: State): Seq[Mention] = mentions

  def cleanupEvents(mentions: Seq[Mention], state: State): Seq[Mention] = {
    splitEvents(mentions, state)
  }
}
