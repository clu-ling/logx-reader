package org.parsertongue.mr

import ai.lum.common.ConfigUtils._
import com.typesafe.config.{ Config, ConfigFactory }
import org.clulab.odin.{ Mention, TextBoundMention, RelationMention, EventMention, State }
import org.parsertongue.mr.logx.odin._

/**
  * Filtering utilities/checks for evaluating the quality of Mentions. <br>
  */
object MentionFilter {

  val config: Config = ConfigFactory.load()

  val minChars: Int = config[Int]("org.parsertongue.mr.mentionFilter.meetsMinLength.minChars")

  val maxArgDistance: Int = config[Int]("org.parsertongue.mr.mentionFilter.maxArgDistance")

  val exceptions: Set[String] = config[List[String]]("org.parsertongue.mr.mentionFilter.meetsMinLength.exceptions").toSet

  // labels (categories) to retain
  val CATEGORIES: Seq[String] = Seq(
    "Constraint",
    "Event",
    "Measurement",
    "Query", 
    "Sentence", 
    "Trigger"
  )

  /** Assesses whether or not a mention meets the minimum length requirement (in number of chars) or if the violation is permitted. */
  def meetsMinLength(
    m: Mention,
    minChars: Int = minChars,
    exceptions: Set[String] = exceptions
  ): Boolean = {
    m.text.length >= minChars || exceptions.contains(m.text)
  }

  /** Ensures entity is valid **/
  def validEntities(ms: Seq[Mention]): Seq[Mention] = {
    val (avoid, entities) = ms.partition(_ matches "Avoid")
    val avoidState: State = State(avoid)
    //avoid.foreach(a => println(s"${a.label}: ${a.text}"))
    entities.filter{ entity =>
      // entity must not intersect with any Avoid mentions
      avoidState.mentionsFor(entity.sentence, entity.tokenInterval).isEmpty &&
        meetsMinLength(entity)
    }
  }

  /**
    * Returns the mentions that are within a given maximum interval length. <br>
    *   Longer intervals are less reliable due to poor parsing and intervening material.
    * @param ms mentions to be filtered
    * @param maxArgDistance maximum allowable interval length
    */
  def keepShortSpans(
    ms: Seq[Mention],
    maxArgDistance: Int = maxArgDistance
  ): Seq[Mention] = {
    val (entities, events) = ms.partition(_.matches("Entity"))
    val shortEnough = events filter { e =>
      val intervals = e.arguments.values.flatten.map(_.tokenInterval).toSeq
      val earliestEnd = max(intervals.map(_.end))
      val latestStart = min(intervals.map(_.start))
      // println(s"Earliest: ${earliestEnd.get}")
      // println(s"Latest: ${latestStart.get}")
      // println(s"Diff: ${math.abs(latestStart.get - earliestEnd.get)}")
      // println(s"Max allowed: $maxArgDistance")
      earliestEnd.isEmpty ||
        latestStart.isEmpty ||
        (math.abs(latestStart.get - earliestEnd.get) <= maxArgDistance)
    }
    entities ++ shortEnough
  }

  /**
    * Given a sequence of integers, returns the least value if one exists. <br>
    * Based on https://stackoverflow.com/a/19045201
    */
  def min(ints: Seq[Int]): Option[Int] = ints match {
    case Nil => None
    case Seq(x: Int) => Option(x)
    case x :: y :: rest => min( (if (x < y) x else y) :: rest )
  }

  /**
    * Given a sequence of integers, returns the greatest value if one exists. <br>
    * Based on https://stackoverflow.com/a/19045201
    */
  def max(ints: Seq[Int]): Option[Int] = ints match {
    case Nil => None
    case Seq(x: Int) => Option(x)
    case x :: y :: rest => max( (if (x > y) x else y) :: rest )
  }

  def keepLongestMentions(ms: Seq[Mention]): Seq[Mention] = {
    keepLongestMentions(ms, State(ms))
  }

  /**
    * When mentions overlap, see if any contain all of the information of others plus more. <br>
    *   Returns the most informative (longer) mentions.
    * @param ms mentions to be filtered
    * @param state the State (for searching for other mentions)
    */
  def keepLongestMentions(ms: Seq[Mention], state: State): Seq[Mention] = {

    def detectBetters(ms: Seq[Mention], label: String, state: State): Seq[Mention] = {
      ms flatMap { m =>
        val overlapping: Seq[Mention] = state.mentionsFor(m.sentence, m.tokenInterval, label)
        if (overlapping.exists(o => betterThan(o, m))) {
          None
        }
        else { 
          Option(m) 
        }
      }
    }
    val (entities, events) = distinctBroad(ms) partition (_.matches("Entity"))
    val keepEntities = detectBetters(entities, "Entity", State(state.allMentions ++ entities))
    //println(s"${events.length} events")
    // labels of interest
    val keepEvents: Seq[Mention] = CATEGORIES.map{ lbl =>
      val subset = events.filter(_ matches lbl)
      detectBetters(subset, lbl, State(state.allMentions ++ subset))
    }.flatten
    keepEntities ++ keepEvents
  }

  /**
    * Remove duplicates of mentions sharing an interval, label, and arguments.
    * @param ms mentions to be filtered
    */
  def distinctBroad(ms: Seq[Mention]): Seq[Mention] = {
    ms.groupBy(m => (m.sentence, m.tokenInterval, m.label, m.arguments, m.isInstanceOf[EventMention])).toSeq.map(_._2.head)
  }

  /**
    * Returns true if a has all of the arguments of b and possibly more
    */
  def subsetArgs(a: Mention, b: Mention): Boolean = {
    val aArgs = a.arguments.values.flatten.toSet
    val bArgs = b.arguments.values.flatten.toSet
    val aNotB = aArgs -- bArgs
    val bNotA = bArgs -- aArgs
    //aNotB.nonEmpty && 
    bNotA.isEmpty
  }

  /**
    * Returns true if a is longer than b, or they're the same length and a's label is a hyponym of b
    */
  def betterThan(a: TextBoundMention, b: TextBoundMention): Boolean = (a, b) match {
    // longer span means better, even with different labels
    case longer if a.tokenInterval.length > b.tokenInterval.length => true
    // shorter span means worse, even with different labels
    case shorter if a.tokenInterval.length < b.tokenInterval.length => false
    // prefer hyponyms
    case hyponym if taxonomy.isa(a.label, b.label) => true
    case _ => false
  }

  /**
    * Returns true if a and b have the same label, and a has all of b's arguments and more.
    */
  def betterThan(a: RelationMention, b: RelationMention): Boolean = (a, b) match {
    // if relations/events have different labels and A is not a hyponym of B, don't compare
    case diffLabel if ((a.label != b.label) && (! taxonomy.isa(a.label, b.label))) => false
    // if a has all the args of b, a is better
    case moreArgs if subsetArgs(a, b) => true
    case _ => false
  }

  def isHyponymOf(a: Mention, b: Mention): Boolean = {
     (a.label == b.label) || taxonomy.isa(a.label, b.label)
  }
  /**
    * Returns true if a and b have the same label and trigger, and a has all of b's arguments and
    * more.
    */
  def betterThan(a: EventMention, b: EventMention): Boolean = (a, b) match {
    // if relations/events have different labels and A is not a hyponym of B, don't compare
    case diffLabel if ((a.label != b.label) && (! taxonomy.isa(a.label, b.label))) =>
      //println(s"diffLabel: ${a.label}(${a.text}) not better than ${b.label}(${b.text})")
      false
    // if the events have a different trigger, it's probably not comparable
    case diffTrigger if ( (! isHyponymOf(a, b)) && a.trigger != b.trigger ) => 
      //println(s"diffTrigger: ${a.label}(${a.text}) not better than ${b.label}(${b.text})")
      false
    // if a has all the args of b, a is better
    case moreArgs if subsetArgs(a, b) => true
      //println(s"\t${a.label}(${a.text}) beats ${b.label}(${b.text})")
      true
    case _ => 
      //println(s"fallthrough: ${a.label}(${a.text}) not better than ${b.label}(${b.text})")
      false
  }

  /**
    * Returns true if a and b have the same label, and a has all of b's arguments or more. <br>
    *   This is a lower bar than event/event, because we prefer to have triggers when possible.
    */
  def betterThan(a: EventMention, b: RelationMention): Boolean = {
    //println("Comparing an EventMention to a RelationMention")
    (a, b) match {
      // if relations/events have different labels and A is not a hyponym of B, don't compare
      case diffLabel if ((a.label != b.label) && (! taxonomy.isa(a.label, b.label))) => false
      // if b has all the args of a and more, b is better despite the missing trigger
      case bMoreArgs if subsetArgs(b, a) =>
        //println("b's got more arguments!")
        false
      case _ => true
    }
  }

  /**
    * Returns true if a and b have the same label, and b has all of a's arguments and more. <br>
    *   This is a higher bar than event/event, because we prefer to have triggers when possible.
    */
  def betterThan(a: RelationMention, b: EventMention): Boolean = {
    //println("Comparing an EventMention to a RelationMention")
    (a, b) match {
      // if relations/events have different labels, don't compare
      case diffLabel if ((a.label != b.label) && (! taxonomy.isa(a.label, b.label))) => false
      // if b has all the args of a or more, b is better despite the missing trigger
      case moreArgs if subsetArgs(a, b) =>
        //println("a's got more arguments!")
        true
      case _ => false
    }
  }

  /**
    * Applies preference heuristics based on Mention type. Returns true when a is preferred to b.
    */
  def betterThan(a: Mention, b: Mention): Boolean = {
    (a, b) match {
    case same if a == b => false // should never be tripped, because distinctBroad should be used
    case (a: TextBoundMention, b: TextBoundMention) => betterThan(a, b)
    case (a: RelationMention, b: RelationMention) => betterThan(a, b)
    case (a: EventMention, b: EventMention) => betterThan(a, b)
    case (a: EventMention, b: RelationMention) => betterThan(a, b)
    case (a: RelationMention, b: EventMention) => betterThan(a, b)
    case notComparable => false
  }
  }

  /**
    * Disallow overlapping args or args with identical normalized text spans
    */
  def disallowOverlappingArgs(ms: Seq[Mention]): Seq[Mention] = ms.filter {
    case event if event matches "Event" =>
      val args: Seq[Mention] = event.arguments.flatMap(_._2).toVector
      // disallow duplicates
      if (args.size != args.distinct.size) { false } else {
        val res = for {
          arg1 <- args
          arg2 <- args
          // don't compare the same mention to itself
          if arg1 != arg2
        } yield {
          // arg should not overlap with another nor should it have the same text
          val isValid = (! arg1.tokenInterval.overlaps(arg2.tokenInterval)) && (arg1.text.toLowerCase != arg2.text.toLowerCase)
          isValid
        }
        // did any validity checks fail?
        ! res.contains(false)
      }
      // n/a for non-events
      case _ => true
    }
}
