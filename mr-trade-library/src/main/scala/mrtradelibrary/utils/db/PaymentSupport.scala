package mrtradelibrary.utils.db

import com.github.tminglei.slickpg.{ ExPostgresProfile, PgEnumSupport }
import mrtradelibrary.utils.Payments

trait PaymentSupport extends PgEnumSupport { _: ExPostgresProfile =>
  trait PaymentImplicits {
    private val typeName = "PAYMENT"

    implicit val paymentTypeMapper =
      createEnumJdbcType(typeName, Payments)

    implicit val paymentTypeListMapper =
      createEnumListJdbcType(typeName, Payments)

    implicit val paymentExtensionMethodsBuilder =
      createEnumColumnExtensionMethodsBuilder(Payments)

    implicit val paymentOptionColumnExtensionMethodsBuilder =
      createEnumOptionColumnExtensionMethodsBuilder(Payments)
  }
}
