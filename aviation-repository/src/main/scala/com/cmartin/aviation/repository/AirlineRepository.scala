package com.cmartin.aviation.repository

import com.cmartin.aviation.repository.Model.AirlineDbo
import zio.{Has, IO, ZIO}

trait AirlineRepository
    extends BaseRepository[AirlineDbo] {

  def findByName(name: String): IO[Throwable, Seq[AirlineDbo]]
  def findByIataCode(code: String): IO[Throwable, Option[AirlineDbo]]
  def findByIcaoCode(code: String): IO[Throwable, Option[AirlineDbo]]
  def findByCountryCode(code: String): IO[Throwable, Seq[AirlineDbo]]
  def deleteByIataCode(code: String): IO[Throwable, Int]
}

object AirlineRepository {
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
