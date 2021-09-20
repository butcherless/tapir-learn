package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Model.AirportDbo
import com.cmartin.aviation.repository.zioimpl.Abstractions.AbstractLongRepository
import com.cmartin.aviation.repository.zioimpl.Tables.Airports
import com.cmartin.aviation.repository.zioimpl.common.Dbio2Zio
import slick.interop.zio.DatabaseProvider
import slick.jdbc.JdbcProfile
import zio.Has
import zio.IO
import zio.ZIO
import zio.ZLayer

object SlickAirportRepository {

  class AirportRepositoryImpl(db: DatabaseProvider, profile: JdbcProfile)
      extends AbstractLongRepository[AirportDbo, Airports](db, profile)
      with AirportRepository {

    import profile.api._

    override val entities = Tables.airports

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

  val live: ZLayer[Has[DatabaseProvider], Throwable, Has[AirportRepository]] = {
    ZLayer.fromServiceM { provider =>
      provider.profile.map { profile =>
        new AirportRepositoryImpl(provider, profile)
      }
    }
  }

  def insert(dbo: AirportDbo): ZIO[Has[AirportRepository], Throwable, Long] =
    ZIO.accessM[Has[AirportRepository]](_.get.insert(dbo))

  def insert(seq: Seq[AirportDbo]): ZIO[Has[AirportRepository], Throwable, Seq[Long]] =
    ZIO.accessM[Has[AirportRepository]](_.get.insert(seq))

  def update(dbo: AirportDbo): ZIO[Has[AirportRepository], Throwable, Int] =
    ZIO.accessM[Has[AirportRepository]](_.get.update(dbo))

  /** Deletes an Aiport by its iata code
    * @param iataCode
    *   airport code
    * @return
    *   number of deleted rows
    */
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

  def count(): ZIO[Has[AirportRepository], Throwable, Int] =
    ZIO.accessM[Has[AirportRepository]](_.get.count())

}
