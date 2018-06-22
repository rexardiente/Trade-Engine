package mrtradelibrary.utils.db

import com.github.tminglei.slickpg.{ ExPostgresProfile, PgEnumSupport }
import mrtradelibrary.utils.Trades

trait TradeSupport extends PgEnumSupport { _: ExPostgresProfile =>
  trait TradeImplicits {
    private val typeName = "TRADE"

    implicit val tradeTypeMapper =
      createEnumJdbcType(typeName, Trades)

    implicit val tradeTypeListMapper =
      createEnumListJdbcType(typeName, Trades)

    implicit val tradeExtensionMethodsBuilder =
      createEnumColumnExtensionMethodsBuilder(Trades)

    implicit val tradeOptionColumnExtensionMethodsBuilder =
      createEnumOptionColumnExtensionMethodsBuilder(Trades)
  }
}
