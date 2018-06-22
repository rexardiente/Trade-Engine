package mrtradelibrary.models.repo

import java.util.UUID
import javax.inject.{ Inject, Singleton }
import scala.concurrent.Future
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import mrtradelibrary.models.domain.{ FundsHistory, RequestFundsHistory }

@Singleton
class FundsHistoryRepo @Inject()(
    dao: mrtradelibrary.models.dao.FundsHistoryDAO,
    protected val dbConfigProvider: DatabaseConfigProvider
  ) extends HasDatabaseConfigProvider[mrtradelibrary.utils.db.PostgresDriver] {
  import profile.api._

  def exists(id: Int): Future[Boolean] =
    db.run(dao.Query(id).exists.result)

  def add[F <: FundsHistory](ff: F): Future[Int] =
    db.run(dao.Query += ff)

  def all[F >: FundsHistory]: Future[Seq[F]] =
    db.run(dao.Query.result)

  def find[F >: FundsHistory](idAccount: UUID): Future[Seq[F]] =
    db.run(dao.Query.filter(_.idAccountRef === idAccount).result)

  def getFundsHistory[R <: RequestFundsHistory, F >: FundsHistory](history: R): Future[Seq[F]] =
    db.run(dao.Query.filter(_.idAccountRef === history.idAccountRef).result)
}

