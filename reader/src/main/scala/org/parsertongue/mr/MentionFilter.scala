package org.parsertongue.mr

import ai.lum.common.ConfigUtils._
import com.typesafe.config.{ Config, ConfigFactory }
import org.clulab.odin.{ Mention, TextBoundMention, RelationMention, EventMention, State }

/**
  * Filtering utilities/checks for evaluating the quality of Mentions. <br>
  */
object MentionFilter {

  val config: Config = ConfigFactory.load()

  val minChars: Int = config[Int]("org.parsertongue.mr.mentionFilter.meetsMinLength.minChars")

  val maxArgDistance: Int = config[Int]("org.parsertongue.mr.mentionFilter.maxArgDistance")

  val exceptions: Set[String] = config[List[String]]("org.parsertongue.mr.mentionFilter.meetsMinLength.exceptions").toSet

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
    def detectBetters(ms: Seq[Mention], label: String): Seq[Mention] = {
      val state = State(ms)
      //println(s"${ms.map(m => m.text + ": " + m.labels).mkString("\n")}")
      ms flatMap { m =>
        val overlapping: Seq[Mention] = state.mentionsFor(m.sentence, m.tokenInterval, label)
        //println(s"overlapping sets: ${overlapping.map(_.text).mkString("""", """")}")
        if (overlapping.exists(o => betterThan(o, m))) {
          //val better = overlapping.find(o => betterThan(o, m))
          //println(s"${better.get.text} (${better.get.foundBy}) is better than ${m.text} (${m.foundBy})\n")
          None
        }
        else {
          //println(s"${m.text} (${m.foundBy}) is good enough!\n")
          Option(m)
        }
      }
    }

    val (entities, events) = distinctBroad(ms) partition (_.matches("Entity"))
    val keepEntities = detectBetters(entities, "Entity")
    // entities.foreach{ m => println(s"keep:\t${m.text}\t(${m.label})\t${m.foundBy}\t${keepEntities.contains(m)}") }
    //println(s"${events.length} events")
    val keepEvents = detectBetters(events, "Event")
    // events.foreach{ m => println(s"keep:\t${m.text}\t(${m.label})\t${m.foundBy}\t${keepEvents.contains(m)}") }
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
    * Returns true if a has all of the arguments of b plus more
    */
  def properSubsetArgs(a: Mention, b: Mention): Boolean = {
    //println(s"A: ${a.label}\t(${a.arguments.values.flatten.map(_.text).mkString(", ")})")
    //println(s"B: ${b.label}\t(${b.arguments.values.flatten.map(_.text).mkString(", ")})")
    val aArgs = a.arguments.values.flatten.toSet
    val bArgs = b.arguments.values.flatten.toSet
    val aNotB = aArgs -- bArgs
    val bNotA = bArgs -- aArgs
    //println(s"aNotB: ${aNotB.map(_.text).mkString(", ")}")
    //println(s"bNotA: ${bNotA.map(_.text).mkString(", ")}")

    aNotB.nonEmpty && bNotA.isEmpty
  }

  /**
    * Returns true if a is longer than b, or they're the same length and a's label is alphabetically
    * earlier.
    */
  def betterThan(a: TextBoundMention, b: TextBoundMention): Boolean = (a, b) match {
    // longer span means better, even with different labels
    case longer if a.tokenInterval.length > b.tokenInterval.length => true
    // shorter span means worse, even with different labels
    case shorter if a.tokenInterval.length < b.tokenInterval.length => false
    // if we have a tie, just break ties with label
    // FIXME: if we make the taxonomy available via the conf, we could pick the mention with the most specific label
    case alphabeticallyEarlier if a.label < b.label => true
    case _ => false
  }

  /**
    * Returns true if a and b have the same label, and a has all of b's arguments and more.
    */
  def betterThan(a: RelationMention, b: RelationMention): Boolean = (a, b) match {
    // if relations/events have different labels, don't compare
    case diffLabel if a.label != b.label => false
    // if a has all the args of b and more, a is better
    case moreArgs if properSubsetArgs(a, b) => true
    case _ => false
  }

  /**
    * Returns true if a and b have the same label and trigger, and a has all of b's arguments and
    * more.
    */
  def betterThan(a: EventMention, b: EventMention): Boolean = (a, b) match {
    // if relations/events have different labels, don't compare
    case diffLabel if a.label != b.label => false
    // if the events have a different trigger, it's probably not comparable
    case diffTrigger if a.trigger != b.trigger => false
    // if a has all the args of b and more, a is better
    case moreArgs if properSubsetArgs(a, b) => true
    case _ => false
  }

  /**
    * Returns true if a and b have the same label, and a has all of b's arguments or more. <br>
    *   This is a lower bar than event/event, because we prefer to have triggers when possible.
    */
  def betterThan(a: EventMention, b: RelationMention): Boolean = {
    //println("Comparing an EventMention to a RelationMention")
    (a, b) match {
      // if relations/events have different labels, don't compare
      case diffLabel if a.label != b.label =>
        //println("Different label")
        false
      // if b has all the args of a and more, b is better despite the missing trigger
      case bMoreArgs if properSubsetArgs(b, a) =>
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
      case diffLabel if a.label != b.label =>
        //println("Different label")
        false
      // if b has all the args of a or more, b is better despite the missing trigger
      case moreArgs if properSubsetArgs(a, b) =>
        //println("a's got more arguments!")
        true
      case _ => false
    }
  }

  /**
    * Applies preference heuristics based on Mention type. Returns true when a is preferred to b.
    */
  def betterThan(a: Mention, b: Mention): Boolean = (a, b) match {
    case same if a == b => false // should never be tripped, because distinctBroad should be used
    case (a: TextBoundMention, b: TextBoundMention) => betterThan(a, b)
    case (a: RelationMention, b: RelationMention) => betterThan(a, b)
    case (a: EventMention, b: EventMention) => betterThan(a, b)
    case (a: EventMention, b: RelationMention) => betterThan(a, b)
    case (a: RelationMention, b: EventMention) => betterThan(a, b)
    case notComparable => false
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
