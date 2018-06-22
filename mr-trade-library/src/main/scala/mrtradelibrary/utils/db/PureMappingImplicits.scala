package mrtradelibrary.utils.db

import java.util.UUID
import play.api.libs.json._
import slick.jdbc.{ GetResult, PositionedParameters, PositionedResult, SetParameter }
import mrtradelibrary.utils._

trait PureMappingImplicits {
  implicit class PgPositionedUUIDResult(val r: PositionedResult) {
    def nextUUID: UUID = UUID.fromString(r.nextString)

    def nextUUIDOption: Option[UUID] = r.nextStringOption().map(UUID.fromString)
  }

  implicit class PgPositionedCurrencyResult(val r: PositionedResult) {
    def nextCurrency: Currencies.Value = Currencies.withName(r.nextString)

    def nextCurrencyOption: Option[Currencies.Value] = r.nextStringOption().map(Currencies.withName)
  }

  implicit class PgPositionedSideResult(val r: PositionedResult) {
    def nextSide: Sides.Value = Sides.withName(r.nextString)

    def nextSidesOption: Option[Sides.Value] = r.nextStringOption().map(Sides.withName)
  }

  implicit class PgPositionedTradeResult(val r: PositionedResult) {
    def nextTrade: Trades.Value = Trades.withName(r.nextString)

    def nextTradeOption: Option[Trades.Value] = r.nextStringOption().map(Trades.withName)
  }

  implicit object SetUUID extends SetParameter[UUID] {
    def apply(v: UUID, pp: PositionedParameters): Unit = {
      pp.setObject(v, java.sql.JDBCType.BINARY.getVendorTypeNumber)
    }
  }

  implicit object SetJsValue extends SetParameter[JsValue] {
    def apply(v: JsValue, pp: PositionedParameters): Unit = {
      pp.setObject(v, java.sql.JDBCType.BINARY.getVendorTypeNumber)
    }
  }

  implicit object SetCurrency extends SetParameter[Currencies.Value] {
    def apply(v: Currencies.Value, pp: PositionedParameters): Unit = {
      pp.setObject(v, java.sql.JDBCType.BINARY.getVendorTypeNumber)
    }
  }

  implicit object SetSide extends SetParameter[Sides.Value] {
    def apply(v: Sides.Value, pp: PositionedParameters): Unit = {
      pp.setObject(v, java.sql.JDBCType.BINARY.getVendorTypeNumber)
    }
  }
}
