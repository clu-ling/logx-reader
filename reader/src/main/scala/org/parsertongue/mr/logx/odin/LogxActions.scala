package org.parsertongue.mr.logx.odin

import org.clulab.odin._
import org.parsertongue.mr.MentionFilter
import org.parsertongue.mr.actions.OdinActions
import org.parsertongue.mr.logx.odin._

class LogxActions extends OdinActions {

  def handleTransportEvent(mentions: Seq[Mention], state: State): Seq[Mention] = {
      mentions map {
          case transport if transport matches "Transport" => 
              // ensure mentions for `shipment` role have the label `Cargo`
              // (i.e., "Promote" shipment's label to `Cargo`)
              // TODO: extend Odin query language with `:OldLabel^NewLabel` syntax for label promotion
              val cargoRole = "shipment"
              val cargoMentions = transport.arguments.getOrElse(cargoRole, Nil)
              val newArgs = transport.arguments + (cargoRole -> cargoMentions.map(mkCargoMention))
              transport match {
                  // invoke copy constructor for Mention subtypes w/ args
                  case em: EventMention => em.copy(arguments = newArgs)
                  case rel: RelationMention => rel.copy(arguments = newArgs)
                  case cm: CrossSentenceMention => cm.copy(arguments = newArgs)
                  case m => m
              }

          case other => other
      }
  }

  def mkCargoMention(m: Mention): Mention = m match {
      case cargo if cargo matches "Cargo" => m
      // discard unit portion of any QuantifiedConcept
      case qc if qc matches "QuantifiedConcept" => 
        qc match {
            case em: EventMention         => 
              val arg = em.arguments.getOrElse("concept", Seq(em)).head
              mkCargoMention(arg)
            case rel: RelationMention     => 
              val arg = rel.arguments.getOrElse("concept", Seq(rel)).head
              mkCargoMention(arg)
            // NOTE: this should never be encountered
            case tb: TextBoundMention     => tb.copy(labels = CARGO_LABELS)
            case cm: CrossSentenceMention => 
              val arg = cm.arguments.getOrElse("concept", Seq(cm)).head
              mkCargoMention(arg)
        }
      case nonCargo => 
        nonCargo match {
            case tb: TextBoundMention     => tb.copy(labels = CARGO_LABELS)
            // NOTE: these shouldn't be necessary
            case em: EventMention         => em.copy(labels = CARGO_LABELS)
            case rel: RelationMention     => rel.copy(labels = CARGO_LABELS)
            case cm: CrossSentenceMention => cm.copy(labels = CARGO_LABELS)
        }

  }

  def distinctArgs(mentions: Seq[Mention], state: State = new State()): Seq[Mention] = mentions map { mention => 
    val newArgs: Map[String, Seq[Mention]] = mention.arguments.keys.map{ arg => 
      arg -> mention.arguments(arg).distinct
    }.toMap

    mention match {
        // invoke copy constructor for Mention subtypes w/ args
        case em: EventMention => em.copy(arguments = newArgs)
        case rel: RelationMention => rel.copy(arguments = newArgs)
        case cm: CrossSentenceMention => cm.copy(arguments = newArgs)
        case m => m
    }
  }

  def cleanupEntities(mentions: Seq[Mention], state: State = new State()): Seq[Mention] = {
    val validEntities  = MentionFilter.validEntities(mentions)
    val prunedEntities = keepLongestByLabel(validEntities, "TimeExpression")
    val entities = MentionFilter.keepLongestMentions(prunedEntities)
    entities
  }

  def finalSweep(mentions: Seq[Mention], state: State = new State()): Seq[Mention] = {
    //mentions.foreach{ m => println(s"MENTION: text:\t${m.text}\t(${m.label})\t${m.foundBy}") }
    val shortEnough = MentionFilter.keepShortSpans(mentions)
    val longest = MentionFilter.keepLongestMentions(shortEnough)
    val filtered = longest.filter{ mn => (mn matches "VerbPhrase") == false }
    val events = MentionFilter.disallowOverlappingArgs(filtered)
    events
  }

  def keepLongestByLabel(mentions: Seq[Mention], label: String, state: State = new State()): Seq[Mention] = {
    val res = mentions.partition(_ matches label)
    val toFilter = res._1
    //val toFilter = (res._1 ++ state.allMentions.filter(_ matches label)).distinct
    val other = res._2
    val mns: Iterable[Mention] = for {
    // find mentions in same sentence
      (k, v) <- toFilter.groupBy(_.sentence)
      m <- v
      //_ = println(s"Size of v: ${v.size}")
      // for overlapping mentions, keep only the longest
      numOverlapping = v.filter(_.tokenInterval.toSet.intersect(m.tokenInterval.toSet).nonEmpty)
      //_ = println(s"num. overlapping: ${numOverlapping.size}")
      //_ = println(s"overlapping: ${numOverlapping.map(_.text).mkString("")}")
      longest = numOverlapping.maxBy(_.tokenInterval.length)
      //_ = println(longest.text)
    } yield longest
    mns.toVector.distinct ++ other
  }

  val CARGO_LABELS = taxonomy.hypernymsFor("Cargo")
}