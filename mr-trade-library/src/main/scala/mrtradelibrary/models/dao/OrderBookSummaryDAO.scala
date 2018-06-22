package mrtradelibrary.models.dao

import javax.inject.{ Inject, Singleton }
import java.time.Instant
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import mrtradelibrary.models.domain.OrderBookSummary

@Singleton
final class OrderBookSummaryDAO @Inject()(
    protected val dbConfigProvider: DatabaseConfigProvider
  ) extends HasDatabaseConfigProvider[mrtradelibrary.utils.db.PostgresDriver] {
  import profile.api._

  protected class OrderBookSummaryTable(tag: Tag) extends Table[OrderBookSummary](tag, "ORDER_BOOK_SUMMARY") {
    def delta = column[Int]("DELTA")
    def createdAt = column[Instant]("CREATED_AT")
    def data = column[String]("DATA")

    def * = (
        delta,
        createdAt,
        data) <> (OrderBookSummary.tupled, OrderBookSummary.unapply)
  }

  object Query extends TableQuery(new OrderBookSummaryTable(_)) {
    @inline def apply(delta: Int) = this.withFilter(_.delta === delta)

  }
}
