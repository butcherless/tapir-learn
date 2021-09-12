package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Model.AirportDbo
import com.cmartin.aviation.repository.zioimpl.common.Dbio2Zio
import slick.interop.zio.DatabaseProvider
import zio.{Has, IO, ZIO, ZLayer}

object SlickAirportRepository {

  val live: ZLayer[Has[DatabaseProvider], Throwable, Has[AirportRepository]] = {
    ZLayer.fromServiceM { db =>
      db.profile.map { profile =>
        import profile.api._

        new AirportRepository {
          private val entities = Tables.airports

          override def insert(dbo: AirportDbo): IO[Throwable, Long] = {
            val action = (entities returning entities.map(_.id)) += dbo
            (action, db).toZio
          }

          override def update(dbo: AirportDbo): IO[Throwable, Int] = {
            val query = entities.filter(_.id === dbo.id)
            (query.update(dbo), db).toZio
          }

          override def delete(iataCode: String): IO[Throwable, Int] = {
            val query = entities.filter(_.iataCode === iataCode)
            (query.delete, db).toZio
          }

          override def findByIataCode(code: String): IO[Throwable, Option[AirportDbo]] = {
            val query = entities.filter(_.iataCode === code)
            (query.result.headOption, db).toZio
          }

          override def findByIcaoCode(code: String): IO[Throwable, Option[AirportDbo]] = {
            val query = entities.filter(_.icaoCode === code)
            (query.result.headOption, db).toZio
          }

          override def findByCountryCode(code: String): IO[Throwable, Seq[AirportDbo]] = {
            val query = for {
              airport <- entities
              country <- airport.country if country.code === code
            } yield airport

            (query.result, db).toZio
          }

          override def findByName(name: String): IO[Throwable, Seq[AirportDbo]] = {
            val query = entities.filter(_.name like s"%$name%")
            (query.result, db).toZio
          }
        }
      }
    }
  }

  def insert(dbo: AirportDbo): ZIO[Has[AirportRepository], Throwable, Long] =
    ZIO.accessM[Has[AirportRepository]](_.get.insert(dbo))

  def update(dbo: AirportDbo): ZIO[Has[AirportRepository], Throwable, Int] =
    ZIO.accessM[Has[AirportRepository]](_.get.update(dbo))

  def delete(iataCode: String): ZIO[Has[AirportRepository], Throwable, Int] =
    ZIO.accessM[Has[AirportRepository]](_.get.delete(iataCode))

  def findByIataCode(code: String): ZIO[Has[AirportRepository], Throwable, Option[AirportDbo]] =
    ZIO.accessM[Has[AirportRepository]](_.get.findByIataCode(code))

  def findByIcaoCode(code: String): ZIO[Has[AirportRepository], Throwable, Option[AirportDbo]] =
    ZIO.accessM[Has[AirportRepository]](_.get.findByIcaoCode(code))

  def findByCountryCode(code: String): ZIO[Has[AirportRepository], Throwable, Seq[AirportDbo]] =
    ZIO.accessM[Has[AirportRepository]](_.get.findByCountryCode(code))

  def findByName(name: String): ZIO[Has[AirportRepository], Throwable, Seq[AirportDbo]] =
    ZIO.accessM[Has[AirportRepository]](_.get.findByName(name))

}
