package com.cmartin.aviation.repository

import com.cmartin.aviation.repository.Model.AirportDbo
import zio.{Has, IO, ZIO}

trait AirportRepository
    extends BaseRepository[AirportDbo] {

  def deleteByIataCode(code: String): IO[Throwable, Int]
  def findByIataCode(code: String): IO[Throwable, Option[AirportDbo]]
  def findByIcaoCode(code: String): IO[Throwable, Option[AirportDbo]]
  def findByCountryCode(code: String): IO[Throwable, Seq[AirportDbo]]
  def findByName(name: String): IO[Throwable, Seq[AirportDbo]]

}

object AirportRepository {
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
  def deleteByIataCode(iataCode: String): ZIO[Has[AirportRepository], Throwable, Int] =
    ZIO.accessM[Has[AirportRepository]](_.get.deleteByIataCode(iataCode))

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
