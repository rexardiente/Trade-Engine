package models

import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import javax.inject.{ Inject, Singleton }
import java.util.UUID
import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global

@Singleton
class SqlExecutor @Inject()(
    protected val dbConfigProvider: DatabaseConfigProvider,
    protected implicit val ec: ExecutionContext)
  extends HasDatabaseConfigProvider[mrtradelibrary.utils.db.PostgresDriver] {
  import profile.api._

  def truncate = {
    db.run(sql"""truncate table "TRADE_WALLETS" cascade;""".as[Int])
  }

  def initDb = {
    db.run(sql"""
      insert into "TRADE_WALLETS" ("ID","ID_ACCOUNT_REF","COLOR","CREATE_AT")
      values ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', '#FF0000', now());

      insert into "TRANSACTIONS" ("ID", "ID_ORDER", "ID_ACCOUNT_REF", "TRADE_TYPE", "SIDE", "PRICE", "SIZE_IN_BASE", "ID_CURRENCY_BASE", "ID_CURRENCY_COUNTER", "CREATED_AT") values (default, '00000000-0000-0000-000A-000000000000', '00000000-0000-0000-0000-000000000001', 'FILL'::trade, 'BUY'::side, '1.0', '2.0', 'BTC'::currency, 'JPY'::currency, now());
      insert into "TRANSACTIONS" ("ID", "ID_ORDER", "ID_ACCOUNT_REF", "TRADE_TYPE", "SIDE", "PRICE", "SIZE_IN_BASE", "ID_CURRENCY_BASE", "ID_CURRENCY_COUNTER", "CREATED_AT") values (default, '00000000-0000-0000-000A-000000000000', '00000000-0000-0000-0000-000000000001', 'FILL'::trade, 'SELL'::side, '1.0', '1.0', 'BTC'::currency, 'JPY'::currency, now());
      insert into "TRANSACTIONS" ("ID", "ID_ORDER", "ID_ACCOUNT_REF", "TRADE_TYPE", "SIDE", "PRICE", "SIZE_IN_BASE", "ID_CURRENCY_BASE", "ID_CURRENCY_COUNTER", "CREATED_AT") values (default, '00000000-0000-0000-000A-000000000000', '00000000-0000-0000-0000-000000000001', 'FILL'::trade, 'SELL'::side, '1.0', '1.0', 'BTC'::currency, 'JPY'::currency, now());
      """.as[Int])
  }
}
