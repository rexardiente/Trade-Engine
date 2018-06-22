package mrtradelibrary.models.repo

import java.util.UUID
import javax.inject.{ Inject, Singleton }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import play.api.libs.json.JsValue
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import mrtradelibrary.models.domain.{ Transactions, TransactionHistory, OpenOrders }
import mrtradelibrary.utils._

@Singleton
class TransactionsRepo @Inject()(
    dao: mrtradelibrary.models.dao.TransactionsDAO,
    protected val dbConfigProvider: DatabaseConfigProvider
  ) extends HasDatabaseConfigProvider[db.PostgresDriver] {
  import profile.api._

  def exists(idOrder: UUID): Future[Boolean] =
    db.run(dao.Query(idOrder).exists.result)

  def add[T <: Transactions](tt: T): Future[Int] =
    db.run(dao.Query += tt)

  def all: Future[Seq[Transactions]] =
    db.run(dao.Query.result)

  def find[T >: Transactions](idAccount: UUID): Future[Seq[T]] =
    db.run(dao.Query.filter(_.idAccountRef === idAccount).result)

  def openOrders[T >: Transactions, O <: OpenOrders](order: O): Future[Seq[T]] = {
    for {
      list <- {
        db.run(
          dao.Query.filter( r =>
            r.idAccountRef === order.idAccountRef &&
            r.idCurrencyBase === order.currencyBase &&
            r.idCurrencyCounter === order.currencyCounter
          ).result
        )
      }

      orders <- {
        val transac  = ListBuffer[Transactions]()

        list.map { count =>
          if (count.tradeType == Trades.Cancel || count.tradeType == Trades.Cancelled ||
              count.tradeType == Trades.Fill)
            transac.filter(_.idOrder == count.idOrder).foreach(transac -= _)

          else if (count.tradeType == Trades.Partially_Fill)
            transac.find(_.idOrder == count.idOrder)
              .foreach { res =>
                val newTransaction = Transactions(
                  res.id,
                  res.idOrder,
                  res.idAccountRef,
                  res.tradeType,
                  res.side,
                  res.price,
                  (res.amount - count.amount),
                  res.idCurrencyBase,
                  res.idCurrencyCounter,
                  res.createdAt)
                transac -= res;
                transac += newTransaction;
              }

          else transac += count
        }
        Future.successful(transac)
      }
    } yield (orders)
  }

  def getTransactionHistory[T >: Transactions, TH <: TransactionHistory](th: TH):
    Future[Seq[T]] = {
      db.run (dao.Query.filter(_.idAccountRef === th.idAccountRef).result)
    }

  def getPrice(idCounter: Currencies.Value, idBase: Currencies.Value): Future[Option[BigDecimal]] =
    db.run(dao.Query.filter(r =>
      r.idCurrencyBase === idBase && r.idCurrencyCounter === idCounter).sortBy(_.createdAt.desc)
      .map(_.price).result.headOption)

  def getNewTransactionRequests[T >: Transactions](index: Int): Future[Seq[T]] =
    db.run(
      dao.Query.filter( r =>
        r.id > index &&
        (r.tradeType === Trades.Cancel || r.tradeType === Trades.Create)
      ).result
    )

  def getCompletedTransactions[T >: Transactions]: Future[Seq[T]] =
    db.run(dao.Query.filter(r =>
      !(r.tradeType === Trades.Cancel || r.tradeType === Trades.Create)).result)

  def updateBalance(orderResults: String): Future[List[String]] = {
    val res = "[" + orderResults + "]"
    db.run(sql"""select "CFN_UPDATE_BALANCE"($res);""".as[String])
      .map(_.toList)
  }

}
