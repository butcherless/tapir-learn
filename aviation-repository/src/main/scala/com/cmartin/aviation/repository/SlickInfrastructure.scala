package com.cmartin.aviation.repository

import com.cmartin.aviation.repository.Model.{CountryDbo, LongDbo, TableNames}
import com.cmartin.aviation.repository.SlickInfrastructure.CountryTable.countries
import com.cmartin.aviation.repository.SlickInfrastructure.Repositories.CountryRepo
import slick.interop.zio.DatabaseProvider
import slick.interop.zio.syntax._
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{Index, ProvenShape}
import zio.{Has, IO, ZIO, ZLayer}

object SlickInfrastructure {

  object CountryTable {

    abstract class LongBasedTable[T <: LongDbo](tag: Tag, tableName: String) extends Table[T](tag, tableName) {
      /* primary key column */
      def id: Rep[Long] = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    }

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

  }

  object Repositories {
    trait CountryRepo {
      def create(countryDbo: CountryDbo): IO[Throwable, Long]
      def findByCode(code: String): IO[Throwable, Option[CountryDbo]]
    }
  }

  object SlickCountryRepository {
    val live: ZLayer[Has[DatabaseProvider], Throwable, Has[CountryRepo]] =
      ZLayer.fromServiceM { db =>
        db.profile.map { profile =>
          import profile.api._

          new CountryRepo {
            override def create(dbo: CountryDbo): IO[Throwable, Long] = {
              val query = (countries returning countries.map(_.id)) += dbo
              ZIO.fromDBIO(query).provide(Has(db))
            }

            override def findByCode(code: String): IO[Throwable, Option[CountryDbo]] = {
              val query = countries.filter(c => c.code === code)
              ZIO.fromDBIO(
                query.result.headOption
              ).provide(Has(db))
            }

          }
        }
      }
  }

}
