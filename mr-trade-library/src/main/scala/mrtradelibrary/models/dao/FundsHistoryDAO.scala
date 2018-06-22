package mrtradelibrary.models.dao

import java.util.UUID
import javax.inject.{ Inject, Singleton }
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import mrtradelibrary.models.domain._
import mrtradelibrary.utils._

@Singleton
final class FundsHistoryDAO @Inject()(
    protected val tradeWalletDAO: TradeWalletDAO,
    protected val currencyNetworkDAO: CurrencyNetworkDAO,
    protected val dbConfigProvider: DatabaseConfigProvider
  ) extends HasDatabaseConfigProvider[mrtradelibrary.utils.db.PostgresDriver] {
  import profile.api._

  protected class FundsHistoryTable(tag: Tag)
  extends Table[FundsHistory](tag, "FUNDS_HISTORY") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def idAccountRef = column[UUID]("ID_ACCOUNT_REF")
    def idCurrency = column[Currencies.Value]("ID_CURRENCY")
    def paymentType = column[Payments.Value]("PAYMENT_TYPE")
    def amount = column[scala.math.BigDecimal]("AMOUNT")
    def createdAt = column[java.time.Instant]("CREATE_AT")

    def * = (id, idAccountRef, idCurrency, paymentType, amount, createdAt) <> (
      FundsHistory.tupled, FundsHistory.unapply)

    def idAccountFk = foreignKey(
      s"FK_ID_ACCOUNT_$tableName",
      idAccountRef,
      tradeWalletDAO.Query)(
      _.idAccountRef,
      onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Cascade)
  }

  object Query extends TableQuery(new FundsHistoryTable(_)) {
    @inline def apply(id: Int) = this.withFilter(_.id === id)
  }
}
