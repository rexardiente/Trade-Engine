package mrtradelibrary.utils.db

import com.github.tminglei.slickpg.{ ExPostgresProfile, PgEnumSupport }
import mrtradelibrary.utils.Sides

trait SideSupport extends PgEnumSupport { _: ExPostgresProfile =>
  trait SideImplicits {
    private val typeName = "SIDE"

    implicit val sideTypeMapper =
      createEnumJdbcType(typeName, Sides)

    implicit val sideTypeListMapper =
      createEnumListJdbcType(typeName, Sides)

    implicit val sideExtensionMethodsBuilder =
      createEnumColumnExtensionMethodsBuilder(Sides)

    implicit val sideOptionColumnExtensionMethodsBuilder =
      createEnumOptionColumnExtensionMethodsBuilder(Sides)
  }
}
