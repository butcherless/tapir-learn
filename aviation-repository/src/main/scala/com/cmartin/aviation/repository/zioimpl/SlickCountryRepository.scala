package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Model.CountryDbo
import com.cmartin.aviation.repository.zioimpl.common.Dbio2Zio
import slick.interop.zio.DatabaseProvider
import zio.{Has, IO, ZIO, ZLayer}

object SlickCountryRepository {

  val live: ZLayer[Has[DatabaseProvider], Throwable, Has[CountryRepository]] = {
    ZLayer.fromServiceM { db =>
      db.profile.map { profile =>
        import profile.api._

        new CountryRepository {
          private val entities = Tables.countries

          override def insert(dbo: CountryDbo): IO[Throwable, Long] = {
            val action = (entities returning entities.map(_.id)) += dbo
            (action, db).toZio
          }

          override def findByCode(code: String): IO[Throwable, Option[CountryDbo]] = {
            val query = entities.filter(_.code === code)
            (query.result.headOption, db).toZio
          }

          override def update(dbo: CountryDbo): IO[Throwable, Int] = {
            val query = entities.filter(_.id === dbo.id)
            (query.update(dbo), db).toZio
          }

          override def delete(code: String): IO[Throwable, Int] = {
            val query = entities.filter(_.code === code)
            (query.delete, db).toZio
          }

          override def count(): IO[Throwable, Int] = {
            (Tables.count(entities), db).toZio
          }
        }

      }
    }
  }

  def findByCode(code: String): ZIO[Has[CountryRepository], Throwable, Option[CountryDbo]] =
    ZIO.accessM[Has[CountryRepository]](_.get.findByCode(code))

  def insert(dbo: CountryDbo): ZIO[Has[CountryRepository], Throwable, Long] =
    ZIO.accessM[Has[CountryRepository]](_.get.insert(dbo))

  def update(dbo: CountryDbo): ZIO[Has[CountryRepository], Throwable, Int] =
    ZIO.accessM[Has[CountryRepository]](_.get.update(dbo))

  def delete(code: String): ZIO[Has[CountryRepository], Throwable, Int] =
    ZIO.accessM[Has[CountryRepository]](_.get.delete(code))

  def count(): ZIO[Has[CountryRepository], Throwable, Int] =
    ZIO.accessM[Has[CountryRepository]](_.get.count())
}
