package com.cmartin.aviation.repository

import com.cmartin.aviation.repository.Model.AirlineDbo
import zio.Has
import zio.RIO
import zio.Task
import zio.ZIO

trait AirlineRepository
    extends BaseRepository[AirlineDbo] {

  def findByName(name: String): Task[Seq[AirlineDbo]]
  def findByIataCode(code: String): Task[Option[AirlineDbo]]
  def findByIcaoCode(code: String): Task[Option[AirlineDbo]]
  def findByCountryCode(code: String): Task[Seq[AirlineDbo]]
  def deleteByIataCode(code: String): Task[Int]
}

object AirlineRepository {
  def insert(dbo: AirlineDbo): RIO[Has[AirlineRepository], Long] =
    ZIO.accessM[Has[AirlineRepository]](_.get.insert(dbo))

  def insert(seq: Seq[AirlineDbo]): RIO[Has[AirlineRepository], Seq[Long]] =
    ZIO.accessM[Has[AirlineRepository]](_.get.insertSeq(seq))

  def update(dbo: AirlineDbo): RIO[Has[AirlineRepository], Int] =
    ZIO.accessM[Has[AirlineRepository]](_.get.update(dbo))

  /** Deletes an Airline by its iata code
    * @param iataCode
    *   airline code
    * @return
    *   number of deleted rows
    */
  def deleteByIataCode(iataCode: String): RIO[Has[AirlineRepository], Int] =
    ZIO.accessM[Has[AirlineRepository]](_.get.deleteByIataCode(iataCode))

  def findByIataCode(code: String): RIO[Has[AirlineRepository], Option[AirlineDbo]] =
    ZIO.accessM[Has[AirlineRepository]](_.get.findByIataCode(code))

  def findByIcaoCode(code: String): RIO[Has[AirlineRepository], Option[AirlineDbo]] =
    ZIO.accessM[Has[AirlineRepository]](_.get.findByIcaoCode(code))

  def findByCountryCode(code: String): RIO[Has[AirlineRepository], Seq[AirlineDbo]] =
    ZIO.accessM[Has[AirlineRepository]](_.get.findByCountryCode(code))

  def findByName(name: String): RIO[Has[AirlineRepository], Seq[AirlineDbo]] =
    ZIO.accessM[Has[AirlineRepository]](_.get.findByName(name))

  def count(): RIO[Has[AirlineRepository], Int] =
    ZIO.accessM[Has[AirlineRepository]](_.get.count())

}
