package mrtradelibrary.models.repo

import java.util.UUID
import javax.inject.{ Inject, Singleton }
import scala.concurrent.Future
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import mrtradelibrary.models.domain.OrderBookSummary

@Singleton
class OrderBookSummaryRepo @Inject()(
    dao: mrtradelibrary.models.dao.OrderBookSummaryDAO,
    protected val dbConfigProvider: DatabaseConfigProvider
  ) extends HasDatabaseConfigProvider[mrtradelibrary.utils.db.PostgresDriver] {
  import profile.api._

  def add[T <: OrderBookSummary](obs: T): Future[Int] =
    db.run(dao.Query += obs)

  def all[T >: OrderBookSummary]: Future[Seq[T]] =
    db.run(dao.Query.result)

  }


