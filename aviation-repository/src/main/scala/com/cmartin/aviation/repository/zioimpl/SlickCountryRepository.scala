package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Model.CountryDbo
import slick.interop.zio.DatabaseProvider
import slick.interop.zio.syntax._
import zio.{Has, IO, ZIO, ZLayer}

object SlickCountryRepository {

  trait SchemaHelper {
    def createSchema(): IO[Throwable, Unit]
    def dropSchema(): IO[Throwable, Unit]
  }

  val init: ZLayer[Has[DatabaseProvider], Throwable, Has[SchemaHelper]] =
    ZLayer.fromServiceM { db =>
      db.profile.map { profile =>
        import profile.api._

        new SchemaHelper {
          override def createSchema(): IO[Throwable, Unit] =
            ZIO.fromDBIO(CountryTable.countries.schema.create)
              .provide(Has(db))

          override def dropSchema(): IO[Throwable, Unit] =
            ZIO.fromDBIO(CountryTable.countries.schema.dropIfExists)
              .provide(Has(db))

        }
      }
    }

  val live: ZLayer[Has[DatabaseProvider], Throwable, Has[CountryRepository]] = {
    ZLayer.fromServiceM { db =>
      db.profile.map { profile =>
        import profile.api._

        new CountryRepository {
          override def insert(countryDbo: CountryDbo): IO[Throwable, Long] = {
            val query = (CountryTable.countries returning CountryTable.countries.map(_.id)) += countryDbo
            ZIO.fromDBIO(query)
              .provide(Has(db))
          }

          override def findByCode(code: String): IO[Throwable, Option[CountryDbo]] = {
            val query = CountryTable.countries.filter(_.code === code)
            ZIO.fromDBIO(query.result.headOption)
              .provide(Has(db))
          }

          override def update(countryDbo: CountryDbo): IO[Throwable, Int] = {
            val query = CountryTable.countries.filter(_.id === countryDbo.id)
            ZIO.fromDBIO(query.update(countryDbo))
              .provide(Has(db))
          }

          override def delete(code: String): IO[Throwable, Int] = {
            val query = CountryTable.countries.filter(_.code === code)

            ZIO.fromDBIO(query.delete)
              .provide(Has(db))
          }
        }
      }
    }
  }
}
