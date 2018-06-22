package mrtradelibrary.models.dao

import java.util.UUID
import javax.inject.{ Inject, Singleton }
import java.time.Instant
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import mrtradelibrary.models.domain._
import mrtradelibrary.utils._

@Singleton
final class TransactionsDAO @Inject()(
    protected val tradeWalletDAO: TradeWalletDAO,
    protected val currencyNetworkDAO: CurrencyNetworkDAO,
    protected val dbConfigProvider: DatabaseConfigProvider
  ) extends HasDatabaseConfigProvider[mrtradelibrary.utils.db.PostgresDriver] {
  import profile.api._

  protected class TransactionsTable(tag: Tag) extends Table[Transactions](tag, "TRANSACTIONS") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def idOrder = column[UUID]("ID_ORDER")
    def idAccountRef = column[UUID]("ID_ACCOUNT_REF")
    def tradeType = column[Trades.Value]("TRADE_TYPE")
    def side = column[Sides.Value]("SIDE")
    def price = column[BigDecimal]("PRICE")
    def amount = column[BigDecimal]("SIZE_IN_BASE")
    def idCurrencyBase = column[Currencies.Value]("ID_CURRENCY_BASE")
    def idCurrencyCounter = column[Currencies.Value]("ID_CURRENCY_COUNTER")
    def createdAt = column[Instant]("CREATED_AT")

    def * = (
      id,
      idOrder,
      idAccountRef,
      tradeType,
      side,
      price,
      amount,
      idCurrencyBase,
      idCurrencyCounter,
      createdAt) <> (Transactions.tupled, Transactions.unapply)

    def idAccountFk = foreignKey(
      s"FK_ID_ACCOUNT_$tableName",
      idAccountRef,
      tradeWalletDAO.Query)(
      _.idAccountRef,
      onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Cascade)
  }

  object Query extends TableQuery(new TransactionsTable(_)) {
    @inline def apply(idOrder: UUID) = this.withFilter(_.idOrder === idOrder)

    @inline def apply(idCurrencyCounter: Currencies.Value, idCurrencyBase: Currencies.Value) =
      this.withFilter( r =>
        r.idCurrencyCounter ===  idCurrencyCounter &&
        r.idCurrencyBase === idCurrencyBase)
  }
}
