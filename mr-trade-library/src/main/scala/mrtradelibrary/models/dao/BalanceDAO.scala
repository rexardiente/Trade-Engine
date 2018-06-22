package mrtradelibrary.models.dao

import java.util.UUID
import javax.inject.{ Inject, Singleton }
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import mrtradelibrary.models.domain._
import scala.math.BigDecimal._
import mrtradelibrary.utils._

@Singleton
final class BalanceDAO @Inject()(
    protected val tradeWalletDAO: TradeWalletDAO,
    protected val currencyNetworkDAO: CurrencyNetworkDAO,
    protected val dbConfigProvider: DatabaseConfigProvider
  ) extends HasDatabaseConfigProvider[db.PostgresDriver] {
  import profile.api._

  protected class BalanceTable(tag: Tag) extends Table[Balance](tag, "BALANCE") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def idAccountRef = column[UUID]("ID_ACCOUNT_REF")
    def idCurrency = column[Currencies.Value]("ID_CURRENCY")
    def balance = column[BigDecimal]("BALANCE")
    def createdAt = column[java.time.Instant]("CREATE_AT")

    def * = (id, idAccountRef, idCurrency, balance, createdAt) <> (
      Balance.tupled, Balance.unapply)

    def idAccountFk = foreignKey(
      s"FK_ID_ACCOUNT_$tableName",
      idAccountRef,
      tradeWalletDAO.Query)(
      _.idAccountRef,
      onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Cascade)
  }

  object Query extends TableQuery(new BalanceTable(_)) {
    @inline def apply(idAccountRef: UUID) = this.withFilter(_.idAccountRef === idAccountRef)
  }
}
