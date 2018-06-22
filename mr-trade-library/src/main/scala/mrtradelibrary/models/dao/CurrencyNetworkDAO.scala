package mrtradelibrary.models.dao

import java.util.UUID
import javax.inject.{ Inject, Singleton }
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import mrtradelibrary.models.domain._
import mrtradelibrary.utils.Currencies
import mrtradelibrary._

@Singleton
final class CurrencyNetworkDAO @Inject()(
    protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[mrtradelibrary.utils.db.PostgresDriver] {
  import profile.api._

  protected class CurrencyNetworkTable(tag: Tag)
    extends Table[CurrencyNetwork](tag, "CURRENCY_NETWORK") {

    def id = column[Currencies.Value]("ID", O.PrimaryKey)
    def name = column[String]("NAME")

    def * = (id, name) <> (
      CurrencyNetwork.tupled, CurrencyNetwork.unapply)
  }

  object Query extends TableQuery(new CurrencyNetworkTable(_)) {
    @inline def apply(id: Currencies.Value) = this.withFilter(_.id === id)
  }
}
