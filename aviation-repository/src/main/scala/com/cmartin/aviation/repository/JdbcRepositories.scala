package com.cmartin.aviation.repository
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.lifted.ForeignKeyQuery
import slick.lifted.Index
import slick.lifted.PrimaryKey
import slick.lifted.ProvenShape

import scala.concurrent.Future

import JdbcDefinitions.BaseDefinitions

object JdbcRepositories {
  import JdbcDefinitions._
  import AbstractRepositories._

  trait AviationRepositories extends BaseDefinitions {
    self: JdbcProfile =>
    import Model._
    import api._

    lazy val countries = TableQuery[CountryTable]

    /* C O U N T R Y
     */
    final class CountryTable(tag: Tag) extends LongBasedTable[CountryDbo](tag, TableNames.countries) {
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

    final class CountrySlickRepository
        extends AbstractLongRepository[CountryDbo, CountryTable]
        with AbstractCountryRepository[DBIO] {

      override val entities: TableQuery[CountryTable] = countries

      override def findByCode(code: String): DBIO[Option[CountryDbo]] = {
        entities.filter(_.code === code).result.headOption
      }
    }
  }

  class DataAccessObject(configPath: String) extends JdbcProfile with AviationRepositories {
    val config = DatabaseConfig.forConfig[JdbcProfile](configPath)

    implicit def runAction[A](action: api.DBIO[A]): Future[A] =
      config.db.run(action)

    val countryRepository = new CountrySlickRepository
  }
}
