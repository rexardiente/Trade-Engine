package mrtradelibrary.utils.db

import com.github.tminglei.slickpg.{ ExPostgresProfile, PgEnumSupport }
import mrtradelibrary.utils.Transaction

trait TransactionSupport extends PgEnumSupport { _: ExPostgresProfile =>
  trait TransactionImplicits {
    private val typeName = "TRANSACTION"

    implicit val transactionTypeMapper =
      createEnumJdbcType(typeName, Transaction)

    implicit val transactionTypeListMapper =
      createEnumListJdbcType(typeName, Transaction)

    implicit val transactionExtensionMethodsBuilder =
      createEnumColumnExtensionMethodsBuilder(Transaction)

    implicit val transactionOptionColumnExtensionMethodsBuilder =
      createEnumOptionColumnExtensionMethodsBuilder(Transaction)
  }
}
