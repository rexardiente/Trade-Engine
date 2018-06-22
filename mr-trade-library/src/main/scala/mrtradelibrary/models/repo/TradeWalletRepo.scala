package mrtradelibrary.models.repo

import java.util.UUID
import javax.inject.{ Inject, Singleton }
import scala.concurrent.Future
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import mrtradelibrary.models.domain.TradeWallet

@Singleton
class TradeWalletRepo @Inject()(
    dao: mrtradelibrary.models.dao.TradeWalletDAO,
    protected val dbConfigProvider: DatabaseConfigProvider
  ) extends HasDatabaseConfigProvider[mrtradelibrary.utils.db.PostgresDriver] {
  import profile.api._

  def exists(id: UUID): Future[Boolean] =
    db.run(dao.Query(id).exists.result)

  def add[T <: TradeWallet](ac: T): Future[Int] =
    db.run(dao.Query += ac)

  def all[T >: TradeWallet]: Future[Seq[T]] =
    db.run(dao.Query.result)

  def delete[U <: UUID](id: U): Future[Int] =
    db.run(dao.Query(id).delete)

  def update[T <: TradeWallet](tw: T): Future[Int] =
    db.run(dao.Query(tw.idAccountRef).update(tw))
  }


