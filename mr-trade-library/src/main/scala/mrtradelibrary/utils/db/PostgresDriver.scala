package mrtradelibrary.utils.db

import com.github.tminglei.slickpg._
import mrtradelibrary.models.domain._

trait PostgresDriver extends ExPostgresProfile
   with PgDate2Support
  with CurrencySupport
  with PaymentSupport
  with TransactionSupport
  with TradeSupport
  with SideSupport {
  override val api = new API
    with DateTimeImplicits
    with CurrencyImplicits
    with PaymentImplicits
    with TransactionImplicits
    with TradeImplicits
    with SideImplicits
    with PureMappingImplicits
    with Date2DateTimePlainImplicits
}

object PostgresDriver extends PostgresDriver
