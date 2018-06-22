package mrtradelibrary.utils.db

import com.github.tminglei.slickpg.{ ExPostgresProfile, PgEnumSupport }
import mrtradelibrary.utils.Currencies

trait CurrencySupport extends PgEnumSupport { _: ExPostgresProfile =>
  trait CurrencyImplicits {
    private val typeName = "CURRENCY"

    implicit val currencyTypeMapper =
      createEnumJdbcType(typeName, Currencies)

    implicit val currencyTypeListMapper =
      createEnumListJdbcType(typeName, Currencies)

    implicit val currencyExtensionMethodsBuilder =
      createEnumColumnExtensionMethodsBuilder(Currencies)

    implicit val currencyOptionColumnExtensionMethodsBuilder =
      createEnumOptionColumnExtensionMethodsBuilder(Currencies)
  }
}
