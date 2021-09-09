package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Model.{CountryDbo, LongDbo, TableNames}
import slick.jdbc.JdbcProfile
import slick.lifted.{Index, ProvenShape}

trait CountryTable { self: JdbcProfile =>
  import api._

  abstract class LongBasedTable[T <: LongDbo](tag: Tag, tableName: String) extends Table[T](tag, tableName) {
    /* primary key column */
    def id: Rep[Long] = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  }

  class Countries(tag: Tag) extends LongBasedTable[CountryDbo](tag, TableNames.countries) {
    // property columns:
    def name: Rep[String] = column[String]("NAME")
    def code: Rep[String] = column[String]("CODE")

    // mapper function
    def * : ProvenShape[CountryDbo] =
      (name, code, id.?).<>(CountryDbo.tupled, CountryDbo.unapply)

    // indexes
    def codeIndex: Index =
      index("code_idx", code, unique = true)
  }

  val countries = TableQuery[Countries]
}

object CountryTable extends CountryTable with JdbcProfile
