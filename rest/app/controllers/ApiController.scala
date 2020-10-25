package controllers

import javax.inject._
import org.apache.commons.lang3.exception.ExceptionUtils
import org.clulab.odin.Mention
import org.clulab.odin.impl.Taxonomy
import scala.util.control.NonFatal
//import scala.util.{ Failure, Success, Try }
import scala.concurrent.{ Future, ExecutionContext }
import scala.concurrent.duration._
import play.api.http.ContentTypes
import play.api.libs.json._
import play.api.mvc._
import play.api.libs.ws._
import akka.actor._
import com.typesafe.config._
import org.clulab.serialization.json._
import org.clulab.odin.serialization.json._
import ai.lum.common.ConfigUtils._
import org.parsertongue.mr.{ BuildInfo, MachineReadingSystem }
import org.parsertongue.mr.rest.query._


/** Handles actions related to information extraction services */
@Singleton
class ApiController @Inject() (
  system: ActorSystem,
  cc: ControllerComponents,
  ws: WSClient
) (implicit ec: ExecutionContext) extends AbstractController(cc) {

  // read config parameters
  // TODO should this be injected instead?
  private val config = ConfigFactory.load()

  val ieSystem = new MachineReadingSystem(config)

  private val readerContext: ExecutionContext = system.dispatchers.lookup("contexts.reader")

  private val jsonBuildInfo: JsValue = Json.obj(
    "name" -> BuildInfo.name,
    "version" -> BuildInfo.version,
    "scalaVersion" -> BuildInfo.scalaVersion,
    "sbtVersion" -> BuildInfo.sbtVersion,
    "libraryDependencies" -> BuildInfo.libraryDependencies,
    "scalacOptions" -> BuildInfo.scalacOptions,
    "gitCurrentBranch" -> BuildInfo.gitCurrentBranch,
    "gitHeadCommit" -> BuildInfo.gitHeadCommit,
    "gitHeadCommitDate" -> BuildInfo.gitHeadCommitDate,
    "gitUncommittedChanges" -> BuildInfo.gitUncommittedChanges,
    "builtAtString" -> BuildInfo.builtAtString,
    "builtAtMillis" -> BuildInfo.builtAtMillis
  )

  /** convenience methods for formatting Play 2 Json */
  implicit class JsonOps(json: JsValue) {
    def format(pretty: Option[Boolean]): Result = pretty match {
      case Some(true) => Ok(Json.prettyPrint(json)).as(ContentTypes.JSON)
      case _ => Ok(json).as(ContentTypes.JSON)
    }
  }

  def buildInfo(pretty: Option[Boolean] = None) = Action.async {
    Future{
      jsonBuildInfo.format(pretty)
    }
  }

  def configInfo(pretty: Option[Boolean]) = Action {
    val options = ConfigRenderOptions.concise.setJson(true)
    val json = Json.parse(config.root.render(options))
    json.format(pretty)
  }

  def index = Action {
    Redirect("/api")
  }
  def openAPI(version: String) = Action.async {
    Future(Ok(views.html.api(version)))
  }

  def taxonomyHyponymsFor(term: String, pretty: Option[Boolean] = None) = Action.async {
    Future {
      Json.toJson(ieSystem.taxonomy.hyponymsFor(term)).format(pretty)
    }
  }

  def taxonomyHypernymsFor(term: String, pretty: Option[Boolean] = None) = Action.async {
    Future{
      Json.toJson(ieSystem.taxonomy.hypernymsFor(term)).format(pretty)
    }
  }

  //def index = Action(Redirect("/api"))

  def getMentions(text: String): Seq[Mention] = {
    ieSystem.reload()
    ieSystem.extract(text)
  }

  def parseQuery(text: String, pretty: Option[Boolean] = None) = Action.async {
    Future {
      try {
        val queryMentions = getMentions(text).filter(_ matches "Query")
        val json: String  = QueryUtils.toQueries(queryMentions).json(pretty=false)
        val playJson      = Json.parse(json)
        //Ok(playJson)
        playJson.format(pretty)
      } catch {
          case NonFatal(e) =>
            val stackTrace = ExceptionUtils.getStackTrace(e)
            val json = Json.toJson(Json.obj("error" -> stackTrace))
            Status(400)(json)
      }
    }
  }

  /** Apply MachineReadingSystem and return Mention json (for use with TAG).
    * */
  def extract(text: String, pretty: Option[Boolean] = None) = Action.async {
    Future {
      try {
        // FIXME: consider adding separate endpoints for interactive development (see reload() call below)
        val mentions   = getMentions(text)
        val jsonAsText = mentions.json(pretty=false)
        val playJson   = Json.parse(jsonAsText)
        playJson.format(pretty)
      } catch {
        case NonFatal(e) =>
          val stackTrace = ExceptionUtils.getStackTrace(e)
          val json = Json.toJson(Json.obj("error" -> stackTrace))
          Status(400)(json)
      }
    }
  }

  /** Use MachineReadingSystem processor annotate text and return Document json"
    * */
  def annotate(text: String, pretty: Option[Boolean] = None) = Action.async {
    Future {
      try {
        // FIXME: consider adding separate endpoints for interactive development (see reload() call below)
        println(s"TEXT: '${text}'")
        ieSystem.reload()
        val doc = ieSystem.annotate(text)
        val jsonAsText = doc.json(pretty=false)
        val playJson   = Json.parse(jsonAsText)
        //Ok(playJson)
        playJson.format(pretty)
      } catch {
        case NonFatal(e) =>
          val stackTrace = ExceptionUtils.getStackTrace(e)
          val json = Json.toJson(Json.obj("error" -> stackTrace))
          Status(400)(json)
      }
    }
  }

}
