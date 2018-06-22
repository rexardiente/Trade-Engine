package models.service

import java.util.UUID
import javax.inject.{ Inject, Singleton }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.libs.json._
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import slick.jdbc.GetResult
import mrtradelibrary.models.domain.{ PairlistSet, PairlistSetResult }
import mrtradelibrary.utils.Currencies
import play.api.libs.functional.syntax._
import mrtradelibrary.models.util.CurrencyDeserializer

@Singleton
class PairlistService @Inject()(
    protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[mrtradelibrary.utils.db.PostgresDriver] {
  import profile.api._
  import utils.PairlistUtility

  implicit val getPairlistSetResult = {
    GetResult(r =>
      PairlistSetResult(
        r.nextCurrency,
        r.nextCurrency,
        r.nextBigDecimal,
        r.nextBigDecimal,
        r.nextBigDecimal
      ))
  }

  def pairlistStatistics(currencyPair: String): Future[List[PairlistSetResult]] = {
    db.run(
      sql"""select * from "CFN_GET_PAIR_LIST"(${currencyPair});"""
      .as[PairlistSetResult])
      .map{_.map { psr =>
        PairlistSetResult(
          psr.currencyBase,
          psr.currencyCounter,
          if (psr.latestPrice == null) BigDecimal(0.0) else psr.latestPrice,
          if (psr.volume24hr == null) BigDecimal(0.0) else psr.volume24hr,
          if (psr.change24hr == null) BigDecimal(0.0) else psr.change24hr
    )}}
    .map(_.toList)
  }
}
