package mrtradelibrary.models.repo

import java.util.UUID
import javax.inject.{ Inject, Singleton }
import scala.collection.mutable.HashMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import mrtradelibrary.models.domain.CurrencyNetwork
import mrtradelibrary.utils._

@Singleton
class CurrencyNetworkRepo @Inject()(
    dao: mrtradelibrary.models.dao.CurrencyNetworkDAO,
    protected val dbConfigProvider: DatabaseConfigProvider
  ) extends HasDatabaseConfigProvider[mrtradelibrary.utils.db.PostgresDriver] {
  import profile.api._

  def exists[C <: Currencies.Value](id: C): Future[Boolean] =
    db.run(dao.Query(id).exists.result)

  def add[CN <: CurrencyNetwork](cn: CN): Future[Int] =
    db.run(dao.Query += cn)

  def all: Future[Seq[CurrencyNetwork]] =
    db.run(dao.Query.result)

  def find[C >: Currencies.Value, O >: Option[C]](id: Currencies.Value): Future[O] =
    db.run(dao.Query.filter(_.id === id).map(_.id).result.headOption)

  def delete[C <: Currencies.Value](id: C): Future[Int] =
    db.run(dao.Query(id).delete)

  def update[CN <: CurrencyNetwork](cn: CN): Future[Int] =
    db.run(dao.Query(cn.id).update(cn))
}
