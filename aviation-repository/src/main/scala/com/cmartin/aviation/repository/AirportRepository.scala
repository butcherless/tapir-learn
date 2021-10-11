package com.cmartin.aviation.repository

import com.cmartin.aviation.repository.Model.AirportDbo
import zio.Has
import zio.RIO
import zio.Task
import zio.ZIO

trait AirportRepository
    extends BaseRepository[AirportDbo] {

  def deleteByIataCode(code: String): Task[Int]
  def findByIataCode(code: String): Task[Option[AirportDbo]]
  def findByIcaoCode(code: String): Task[Option[AirportDbo]]
  def findByCountryCode(code: String): Task[Seq[AirportDbo]]
  def findByName(name: String): Task[Seq[AirportDbo]]

}

object AirportRepository {
  def insert(dbo: AirportDbo): RIO[Has[AirportRepository], Long] =
    ZIO.accessM[Has[AirportRepository]](_.get.insert(dbo))

  def insert(seq: Seq[AirportDbo]): RIO[Has[AirportRepository], Seq[Long]] =
    ZIO.accessM[Has[AirportRepository]](_.get.insertSeq(seq))

  def update(dbo: AirportDbo): RIO[Has[AirportRepository], Int] =
    ZIO.accessM[Has[AirportRepository]](_.get.update(dbo))

  /** Deletes an Aiport by its iata code
    * @param iataCode
    *   airport code
    * @return
    *   number of deleted rows
    */
  def deleteByIataCode(iataCode: String): RIO[Has[AirportRepository], Int] =
    ZIO.accessM[Has[AirportRepository]](_.get.deleteByIataCode(iataCode))

  def findByIataCode(code: String): RIO[Has[AirportRepository], Option[AirportDbo]] =
    ZIO.accessM[Has[AirportRepository]](_.get.findByIataCode(code))

  def findByIcaoCode(code: String): RIO[Has[AirportRepository], Option[AirportDbo]] =
    ZIO.accessM[Has[AirportRepository]](_.get.findByIcaoCode(code))

  def findByCountryCode(code: String): RIO[Has[AirportRepository], Seq[AirportDbo]] =
    ZIO.accessM[Has[AirportRepository]](_.get.findByCountryCode(code))

  def findByName(name: String): RIO[Has[AirportRepository], Seq[AirportDbo]] =
    ZIO.accessM[Has[AirportRepository]](_.get.findByName(name))

  def count(): RIO[Has[AirportRepository], Int] =
    ZIO.accessM[Has[AirportRepository]](_.get.count())

}
