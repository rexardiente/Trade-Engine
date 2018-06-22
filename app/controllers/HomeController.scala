package controllers

import javax.inject._
import javax.inject._
import scala.concurrent.{ Future, ExecutionContext }
import scala.concurrent.ExecutionContext.Implicits.global._
import akka.actor._
import akka.stream._
import play.api._
import play.api.mvc._
import play.api.mvc.WebSocket._
import play.api.libs.json._
import play.api.libs.streams._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats._
import actors._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() (
    implicit system: ActorSystem,
    implicit val materializer: Materializer,
    implicit val ec: ExecutionContext,
    implicit val configuration: play.api.Configuration,
    @Named("AccountServiceActor") accountServiceActor: ActorRef,
    @Named("ClientManagerActor") clientManagerActor: ActorRef
  ) extends Controller {
  implicit val messageFlowTransformer =
    MessageFlowTransformer.jsonMessageFlowTransformer[JsValue, JsValue]

  //for unit testing
  def getAccountSvr = accountServiceActor
  def getClientMgr = clientManagerActor

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */

  def socket = WebSocket.accept[JsValue, JsValue] { implicit request =>
    ActorFlow.actorRef(out => AccountManagerActor.props(out, accountServiceActor, clientManagerActor))
  }

  def hello = Action {
    Ok("hello")
  }
}
