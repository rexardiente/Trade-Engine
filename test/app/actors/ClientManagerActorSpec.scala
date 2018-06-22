package actors

import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.bind
import play.api.Application
import java.util.UUID
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatest._
import mrtradelibrary.utils._
import mrtradelibrary.models.domain._

class ClientManagerActorSpec extends PlaySpec
  with GuiceOneAppPerSuite
  with ScalaFutures {

  implicit override lazy val app = new GuiceApplicationBuilder().build

  def controller(implicit app: Application): controllers.HomeController = Application.instanceCache[controllers.HomeController].apply(app)

  // "ClientManagerActor" should {
  //   "get pair list" in {
  //   }
  // }
}
