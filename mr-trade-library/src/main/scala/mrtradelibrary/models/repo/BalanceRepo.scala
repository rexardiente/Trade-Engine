package mrtradelibrary.models.repo

import java.util.UUID
import javax.inject.{ Inject, Singleton }
import scala.concurrent.Future
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import mrtradelibrary.models.domain.Balance
import mrtradelibrary.utils._

@Singleton
class BalanceRepo @Inject()(
    dao: mrtradelibrary.models.dao.BalanceDAO,
    protected val dbConfigProvider: DatabaseConfigProvider
  ) extends HasDatabaseConfigProvider[db.PostgresDriver] {
  import profile.api._

  def exists[B >: Boolean](idAccount: UUID): Future[B] =
    db.run(dao.Query.filter(_.idAccountRef === idAccount).exists.result)

  def accountBalanceExists[B >: Boolean](idAccount: UUID, idCurrency: Currencies.Value): Future[B] =
    db.run(
      dao.Query.filter( r =>
        r.idAccountRef === idAccount && r.idCurrency === idCurrency
      ).exists
      .result
    )

  def add[B <: Balance](bb: B): Future[Int] =
    db.run(dao.Query += bb)

  def all[B >: Balance]: Future[Seq[B]] =
    db.run(dao.Query.result)

  def getAccountBalances(idAccount: UUID): Future[Seq[Balance]] =
    db.run(dao.Query(idAccount).result)

  def delete[U <: UUID](id: U): Future[Int] =
    db.run(dao.Query(id).delete)

  def findByAccountId[B >: Balance](id: UUID, cc: Currencies.Value): Future[Seq[B]] =
    db.run(dao.Query.filter(r => r.idAccountRef === id && r.idCurrency === cc).result)

  def getByCurrencyAndIdAccount[B >: Balance](id: UUID, cc: Currencies.Value):
    Future[Option[B]] = {
      db.run(
        dao.Query.filter( r =>
          r.idAccountRef === id && r.idCurrency === cc
        ).result
         .headOption
      )}


  def updateBalance(id: UUID, cc: Currencies.Value, balance: BigDecimal):
    Future[Int] = {
      db.run(
        dao.Query.filter( r =>
          r.idAccountRef === id && r.idCurrency === cc
        ).map(_.balance)
        .update(balance)
      )
    }
}
