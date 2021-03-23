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

  def mergeLabels(oldLabels: Seq[String], newLabels: Seq[String]): Seq[String] = {
    (newLabels ++ oldLabels).distinct
  }

  def promoteArgTo(m: Mention, role: String, hyponym: String): Mention = {
    val desiredLabels = taxonomy.hypernymsFor(hyponym)

    val newArgs: Map[String, Seq[Mention]] = m.arguments match {
      case hasArg if m.arguments.contains(role) =>
        val newArgs: Seq[Mention] = m.arguments(role).map { 
          case tb: TextBoundMention     => tb.copy(labels = mergeLabels(tb.labels, desiredLabels))
          case em: EventMention         => em.copy(labels = mergeLabels(em.labels, desiredLabels))
          case rel: RelationMention     => rel.copy(labels = mergeLabels(rel.labels, desiredLabels))
          case cm: CrossSentenceMention => cm.copy(labels = mergeLabels(cm.labels, desiredLabels))
        }
        Map(role -> newArgs)
      case _ => Map.empty[String, Seq[Mention]]
    }

    m match {
      case tb: TextBoundMention     => tb
      case em: EventMention         => em.copy(arguments = em.arguments ++ newArgs)
      case rel: RelationMention     => rel.copy(arguments = rel.arguments ++ newArgs)
      case cm: CrossSentenceMention => cm.copy(arguments = cm.arguments ++ newArgs)
    }
  }

  def handleQuantifiedCargo(mentions: Seq[Mention], state: State = new State()): Seq[Mention] = mentions.map {
    case qc if qc matches "QuantifiedCargo" => promoteArgTo(qc, role = "concept", hyponym = "Cargo")
    case other => other
  }

  def mkCargoMention(m: Mention): Mention = m match {
      case cargo if cargo matches "Cargo" => m
      // discard unit portion of any QuantifiedCargo
      case qc if qc matches "QuantifiedCargo" => 
        promoteArgTo(m, role = "concept", hyponym = "Cargo")
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
        case em: EventMention         => em.copy(arguments = newArgs)
        case rel: RelationMention     => rel.copy(arguments = newArgs)
        case cm: CrossSentenceMention => cm.copy(arguments = newArgs)
        case m                        => m
    }
  }

  def cleanupEntities(mentions: Seq[Mention], state: State = new State()): Seq[Mention] = {
    val validEntities  = MentionFilter.validEntities(mentions)
    val res1 = keepLongestByLabel(validEntities, "TimeExpression")
    // now cleanup concepts and keep longest (the nested concept containing all simpler cases)
    val prunedEntities = keepLongestByLabel(res1, "Concept")
    val entities = MentionFilter.keepLongestMentions(prunedEntities)
    entities
  }

  def finalSweep(mentions: Seq[Mention], state: State = new State()): Seq[Mention] = {
    val labelsToDiscard = Seq("VerbPhrase", "Modifier")
    //mentions.foreach{ m => println(s"MENTION: text:\t${m.text}\t(${m.label})\t${m.foundBy}") }
    val shortEnough = MentionFilter.keepShortSpans(mentions)
    val longest = MentionFilter.keepLongestMentions(shortEnough)
    //Discard VerbPhrase and Modifier mentions
    val filtered = longest.filterNot(s => labelsToDiscard.exists(m => {s matches m}))
    //val events = MentionFilter.disallowOverlappingArgs(filtered)
    val remaining = filtered

    def relabelQuery(orig: String, need: Mention): String = need match {
      case port   if port matches "Location" => "LocationQuery"
      case vessel if vessel matches "Vessel" => "VesselQuery"
      case cargo  if cargo matches "Cargo"   => "CargoQuery"
      case other                             => orig
    }

    // relabel queries
    remaining.map {
      case q if q matches "Query" => 
        q match {
          case em: EventMention =>
            val need = em.arguments("need").head 
            val label = relabelQuery(em.label, need)
            em.copy(labels = (Seq(label) ++ em.labels).distinct)
          case rel: RelationMention =>
            val need = rel.arguments("need").head 
            val label = relabelQuery(rel.label, need)
            rel.copy(labels = (Seq(label) ++ rel.labels).distinct)
          // FIXME: we're assuming a Query can only be an event or relation mention.
          case other => other
        }
      case other => other
    }
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