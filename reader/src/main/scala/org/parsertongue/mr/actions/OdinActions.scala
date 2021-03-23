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
    val res1 = keepLongest(mentions, state)
    splitEvents(res1, state)
  }

  /**
   * NOTE: this should be handled by extending Odin to support :Type in a (?<argname> ) named capture (i.e., ?<argname:ArgType> [constraint])
  **/
  def argsAsEntities(mentions: Seq[Mention], state: State): Seq[Mention] = {
    mentions flatMap {
      case em: EventMention    => 
        // FIXME: convert each arg to have label Entity if it doesn't match Entity
        //mention.arguments
        val newArgs = for {
          pair <- em.arguments
          name = pair._1
          args = pair._2
        } yield {
          val newArgs = args.map{ 
            case acceptable if acceptable matches "Entity" => acceptable
            case unacceptable => replaceLabels(unacceptable, "Entity")
          }
          (name, newArgs)
        }
        Seq(em.copy(arguments = newArgs.toMap))
      case rm: RelationMention => 
        // FIXME: convert each arg to have label Entity if it doesn't match Entity
        //mention.arguments
        val newArgs = for {
          pair <- rm.arguments
          name = pair._1
          args = pair._2
        } yield {
          val newArgs = args.map{ 
            case acceptable if acceptable matches "Entity" => acceptable
            case unacceptable => replaceLabels(unacceptable, "Entity")
          }
          (name, newArgs)
        }
        Seq(rm.copy(arguments = newArgs.toMap))
      case other => Seq(other)
    }
  }

  /** Keeps the longest mention for each group of overlapping mentions **/
  def keepLongest(mentions: Seq[Mention], state: State = new State()): Seq[Mention] = {
    val mns: Iterable[Mention] = for {
    // find mentions of the same label and sentence overlap
      (k, v) <- mentions.groupBy(m => (m.sentence, m.label))
      m <- v
      // for overlapping mentions, keep only the longest
      longest = v.filter(_.tokenInterval.overlaps(m.tokenInterval)).maxBy(_.tokenInterval.length)
    } yield longest
    mns.toVector.distinct
  }

  def replaceLabels(mention: Mention, newLabel: String = "Entity"): Mention = mention match {
    case tb: TextBoundMention => tb.copy(labels = Seq(newLabel))
    case rm: RelationMention => rm.copy(labels = Seq(newLabel))
    case em: EventMention => em.copy(labels = Seq(newLabel))
    // FIXME: CrossSentenceMention has no copy method
    case other => mention
    //case cm: CrossSentenceMention => cm.copy(labels = Seq(newLabel))
  }
}
