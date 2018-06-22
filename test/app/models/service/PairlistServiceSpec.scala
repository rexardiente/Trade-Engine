package models.service

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
import play.api.db.evolutions._
import mrtradelibrary.utils.Currencies


class PairlistServiceSpec extends PlaySpec
  //with OneAppPerSuite
  with GuiceOneAppPerSuite
  with ScalaFutures
  with BeforeAndAfterEach
  with BeforeAndAfterAll {

  val idOrders = List(UUID.randomUUID, UUID.randomUUID)

  implicit override lazy val app = new GuiceApplicationBuilder().
    configure(
      "slick.dbs.default.profile" -> "mrtradelibrary.utils.db.PostgresDriver$",
      "slick.dbs.default.db.driver" -> "org.postgresql.Driver",
      "slick.dbs.default.db.url" -> "jdbc:postgresql://127.0.0.1/mr-balance",
      "slick.dbs.default.db.user" -> "mr-balance",
      "slick.dbs.default.db.password" -> "",
      "slick.dbs.default.db.connectionTimeout" -> "30 seconds",
      "slick.dbs.default.db.keepAliveConnection" -> true).build

  def pairlistService(implicit app: Application): PairlistService = Application.instanceCache[PairlistService].apply(app)

  def sqlExecutor(implicit app: Application): models.SqlExecutor = Application.instanceCache[models.SqlExecutor].apply(app)

  override def beforeEach() {
    whenReady(sqlExecutor.truncate) { res =>
      println("cleanup")
    }
  }

  // override def afterEach() {
  //   whenReady(sqlExecutor.truncate) { res =>
  //     println("cleanup")
  //   }
  // }

  override def beforeAll() = {
    OfflineEvolutions.applyScript(
      new java.io.File("."),
      this.getClass.getClassLoader,
      app.injector.instanceOf[play.api.db.DBApi],
      "default",
      true
    )
  }

  "PairlistService" should {
    "get pair list" in {
      whenReady(sqlExecutor.initDb) { r =>
        val data = Json.parse("""[
          {"code_currency_base":"BTC", "code_currency_counter":"JPY"},
          {"code_currency_base":"XRP", "code_currency_counter":"JPY"}
        ]""")

        whenReady(pairlistService.initialPairlist(data)) { res =>
          assert(res(0).currencyBase == Currencies.Btc)
          assert(res(0).currencyCounter == Currencies.Jpy)
          assert(res(0).latestPrice == BigDecimal("1.0"))
          assert(res(0).volume24hr == BigDecimal("4.0"))
          assert(res(0).change24hr == BigDecimal("1.0"))
        }

        whenReady(pairlistService.initialPairlist(data)) { res =>
          assert(res(1).currencyBase == Currencies.Xrp)
          assert(res(1).currencyCounter == Currencies.Jpy)
          assert(res(1).latestPrice == BigDecimal("0"))
          assert(res(1).volume24hr == BigDecimal("0"))
          assert(res(1).change24hr == BigDecimal("0"))
        }
      }
    }
  }
}
