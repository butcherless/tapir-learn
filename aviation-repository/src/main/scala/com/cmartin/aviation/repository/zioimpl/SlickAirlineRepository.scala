package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Model.AirlineDbo
import com.cmartin.aviation.repository.zioimpl.Abstractions.AbstractLongRepository
import com.cmartin.aviation.repository.zioimpl.Tables.Airlines
import com.cmartin.aviation.repository.zioimpl.common.Dbio2Zio
import slick.interop.zio.DatabaseProvider
import slick.jdbc.JdbcProfile
import zio.Has
import zio.IO
import zio.ZIO
import zio.ZLayer

object SlickAirlineRepository {

  class AirlineRepositoryImpl(db: DatabaseProvider, profile: JdbcProfile)
      extends AbstractLongRepository[AirlineDbo, Airlines](db, profile)
      with AirlineRepository {

    import profile.api._

    override val entities = Tables.airlines

    override def deleteByIataCode(iataCode: String): IO[Throwable, Int] = {
      val query = entities.filter(_.iataCode === iataCode)
      (query.delete, db).toZio
    }

    override def findByIataCode(code: String): IO[Throwable, Option[AirlineDbo]] = {
      val query = entities.filter(_.iataCode === code)
      (query.result.headOption, db).toZio
    }

    override def findByIcaoCode(code: String): IO[Throwable, Option[AirlineDbo]] = {
      val query = entities.filter(_.icaoCode === code)
      (query.result.headOption, db).toZio
    }

    override def findByCountryCode(code: String): IO[Throwable, Seq[AirlineDbo]] = {
      val query = for {
        airline <- entities
        country <- airline.country if country.code === code
      } yield airline

      (query.result, db).toZio
    }

    override def findByName(name: String): IO[Throwable, Seq[AirlineDbo]] = {
      val query = entities.filter(_.name like s"%$name%")
      (query.result, db).toZio
    }

  }

  val live: ZLayer[Has[DatabaseProvider], Throwable, Has[AirlineRepository]] = {
    ZLayer.fromServiceM { provider =>
      provider.profile.map { profile =>
        new AirlineRepositoryImpl(provider, profile)
      }
    }
  }

  def insert(dbo: AirlineDbo): ZIO[Has[AirlineRepository], Throwable, Long] =
    ZIO.accessM[Has[AirlineRepository]](_.get.insert(dbo))

  def insert(seq: Seq[AirlineDbo]): ZIO[Has[AirlineRepository], Throwable, Seq[Long]] =
    ZIO.accessM[Has[AirlineRepository]](_.get.insert(seq))

  def update(dbo: AirlineDbo): ZIO[Has[AirlineRepository], Throwable, Int] =
    ZIO.accessM[Has[AirlineRepository]](_.get.update(dbo))

  /** Deletes an Airline by its iata code
    * @param iataCode
    *   airline code
    * @return
    *   number of deleted rows
    */
  def deleteByIataCode(iataCode: String): ZIO[Has[AirlineRepository], Throwable, Int] =
    ZIO.accessM[Has[AirlineRepository]](_.get.deleteByIataCode(iataCode))

  def findByIataCode(code: String): ZIO[Has[AirlineRepository], Throwable, Option[AirlineDbo]] =
    ZIO.accessM[Has[AirlineRepository]](_.get.findByIataCode(code))

  def findByIcaoCode(code: String): ZIO[Has[AirlineRepository], Throwable, Option[AirlineDbo]] =
    ZIO.accessM[Has[AirlineRepository]](_.get.findByIcaoCode(code))

  def findByCountryCode(code: String): ZIO[Has[AirlineRepository], Throwable, Seq[AirlineDbo]] =
    ZIO.accessM[Has[AirlineRepository]](_.get.findByCountryCode(code))

  def findByName(name: String): ZIO[Has[AirlineRepository], Throwable, Seq[AirlineDbo]] =
    ZIO.accessM[Has[AirlineRepository]](_.get.findByName(name))

  def count(): ZIO[Has[AirlineRepository], Throwable, Int] =
    ZIO.accessM[Has[AirlineRepository]](_.get.count())

}
