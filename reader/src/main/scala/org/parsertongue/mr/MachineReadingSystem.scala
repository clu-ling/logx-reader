package org.parsertongue.mr

import ai.lum.common.ConfigUtils._
import org.parsertongue.mr.processors.ProxiedProcessor
import org.parsertongue.mr.logx.processors.LogxProcessor
import org.parsertongue.mr.entities.{ EntityFinder, OdinEntityFinder }
import org.parsertongue.mr.events.{ EventFinder, OdinEventFinder }
import org.parsertongue.mr.meta.{ Hedging, Negation }
import org.parsertongue.mr.utils.{ RuleUtils }
import com.typesafe.config.{ Config, ConfigFactory }
import com.typesafe.scalalogging.LazyLogging
import org.clulab.odin.{ Action, Actions, Mention, State }
import org.clulab.odin.impl.Taxonomy
import org.clulab.processors.{ Document => CluDocument, Processor }
import org.clulab.processors.clu.{ CluProcessor }

import scala.util.{ Failure, Success, Try }

/**
  * Labrador Information Extraction system.
  */
class MachineReadingSystem(val config: Config) extends LazyLogging {

  var entityFinderName: String   = config[String]("org.parsertongue.mr.entities.entityFinder.name")
  var entityRulesPath: String    = config[String]("org.parsertongue.mr.entities.entityFinder.rulesPath")

  var eventFinderName: String    = config[String]("org.parsertongue.mr.events.eventFinder.name")
  var eventRulesPath: String     = config[String]("org.parsertongue.mr.events.eventFinder.rulesPath")


  val rulesPrefix = config[String]("org.parsertongue.mr.rulesPrefix")

  val taxonomy: Taxonomy = RuleUtils.readTaxonomy(s"${rulesPrefix}/taxonomy.yml")
  lazy val proc: Processor = mkProcessor(config[String]("org.parsertongue.mr.processor"))

  var entityFinder: EntityFinder = mkEntityFinder(entityFinderName)
  var eventFinder: EventFinder   = mkEventFinder(eventFinderName)

  /** Reloads grammars.  Useful for interactive development */
  def reload(): Unit = {
    println(s"event rules: ${eventRulesPath}")
    entityFinder = mkEntityFinder(entityFinderName)
    eventFinder = mkEventFinder(eventFinderName)
  }

  def mkProcessor(procName: String): Processor = {

    logger.info(s"Loading Processor '$procName' ...")

    procName match {
      // FIXME: retrieve URL from conf and pass to ProxiedProcessor
      case "ProxiedProcessor"       => new ProxiedProcessor(config[String]("org.parsertongue.mr.proxiedProcessorUrl"))
      case "CluProcessor"           => new CluProcessor
      case "logx"                   => new LogxProcessor
      case _                        => throw new Exception(s"Unsupported Processor '$procName'")
    }
  }

  def mkEntityFinder(entityFinderName: String): EntityFinder = {

    entityFinderName match {

      case "OdinEntityFinder" =>
        val rules = RuleUtils.read(entityRulesPath)

        val actions: Actions = {
          //val loader = ClassLoader.getSystemClassLoader
          Class.forName(config[String]("org.parsertongue.mr.entities.entityFinder.actions")).newInstance().asInstanceOf[Actions]
         }

        // FIXME: this shouldn't be required to be an action in .actions
        val globalAction: Action = {
          val globalActionName = config[String]("org.parsertongue.mr.entities.entityFinder.globalAction")
          val am = new org.clulab.odin.impl.ActionMirror(actions)
          am.reflect(globalActionName)
        }

        new OdinEntityFinder(
          rules        = rules,
          actions      = actions,
          globalAction = globalAction
        )

      case _ =>
        throw new Exception(s"EntityFinder '${entityFinderName}' unsupported")
    }
  }


  def mkEventFinder(eventFinderName: String): EventFinder = {

    eventFinderName match {

      case "OdinEventFinder" =>
        val rules = RuleUtils.read(eventRulesPath)
        println(rules)
        val actions: Actions = {
          val loader = ClassLoader.getSystemClassLoader
          Class.forName(config[String]("org.parsertongue.mr.events.eventFinder.actions")).newInstance().asInstanceOf[Actions]
        }

        // FIXME: this shouldn't be required to be an action in .actions
        val globalAction: Action = {
          val globalActionName = config[String]("org.parsertongue.mr.events.eventFinder.globalAction")
          val am = new org.clulab.odin.impl.ActionMirror(actions)
          am.reflect(globalActionName)
        }

        new OdinEventFinder(
          rules        = rules,
          actions      = actions,
          globalAction = globalAction
        )

      case _ =>
        throw new Exception(s"EventFinder '${eventFinderName}' unsupported")
    }
  }

  def extract(text: String): Seq[Mention] = {
    val doc = annotate(text)
    extract(doc)
  }

  def extract(document: CluDocument): Seq[Mention] = {
    val unfilteredEntities = entityFinder.extract(document)
//    println(s"BEFORE filtering entities:")
//    summarizeMentions(unfilteredEntities)
    val validEntities = MentionFilter.validEntities(unfilteredEntities)
    val entities = MentionFilter.keepLongestMentions(validEntities)
//    println(s"AFTER filtering entities:")
//    summarizeMentions(entities)
    val unFilteredEvents = eventFinder.extract(document, State(entities))
//    println(s"BEFORE filtering events:")
//    summarizeMentions(unFilteredEvents.filter(_ matches "Event"))
    val shortEnough = MentionFilter.keepShortSpans(unFilteredEvents)
//    println(s"AFTER keepShortSpans:")
//    summarizeMentions(shortEnough.filter(_ matches "Event"))
    val longest = MentionFilter.keepLongestMentions(shortEnough)
//    println(s"AFTER keepLongestMentions:")
//    summarizeMentions(longest.filter(_ matches "Event"))
    val filtered = longest.filter{ mn => (mn matches "VerbPhrase") == false }
    val events = MentionFilter.disallowOverlappingArgs(filtered)
//    println(s"AFTER disallowOverlappingArgs:")
//    summarizeMentions(events.filter(_ matches "Event"))
    events
  }

  def summarizeMentions(mentions: Seq[Mention]): Unit = {
    println("#"* 40)
    mentions.sortBy(_.label).foreach(m => println(s"${m.label} (${m.foundBy}): '${m.text}'"))
    println(s"${"#"* 40}\n")
  }

  def isNegated(m: Mention): Boolean = Negation.isNegated(m)

  def isHedged(m: Mention): Boolean = Hedging.isHedged(m)

  def annotate(text: String): CluDocument = {
    proc.annotate(text, keepText = true)
  }

}

object MachineReadingSystem {
  def apply(): MachineReadingSystem = {
    val conf = ConfigFactory.load()
    new MachineReadingSystem(conf)
  }
}

