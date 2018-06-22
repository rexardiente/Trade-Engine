package mrtradelibrary.models.dao

import java.util.UUID
import javax.inject.{ Inject, Singleton }
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import mrtradelibrary.models.domain._

@Singleton
final class TradeWalletDAO @Inject()(
    protected val dbConfigProvider: DatabaseConfigProvider
  ) extends HasDatabaseConfigProvider[mrtradelibrary.utils.db.PostgresDriver] {
  import profile.api._

  protected class TradeWalletTable(tag: Tag) extends Table[TradeWallet](tag, "TRADE_WALLETS") {
    def id = column[UUID]("ID", O.PrimaryKey)
    def idAccountRef = column[UUID]("ID_ACCOUNT_REF")
    def color = column[String]("COLOR")
    def createdAt = column[java.time.Instant]("CREATE_AT")

    def * = (id, idAccountRef, color, createdAt) <> (
      TradeWallet.tupled, TradeWallet.unapply)

    def idxIdAccountRef =
      index(s"IDX_ID_TRADE_WALLETS_REF_$tableName", idAccountRef, unique = true)
  }

  object Query extends TableQuery(new TradeWalletTable(_)) {
    @inline def apply(idAccountRef: UUID) = this.withFilter(_.idAccountRef === idAccountRef)
  }
}
