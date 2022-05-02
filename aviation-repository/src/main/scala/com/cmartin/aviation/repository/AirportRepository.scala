package com.cmartin.aviation.repository

import com.cmartin.aviation.repository.Model.AirportDbo
import com.cmartin.aviation.repository.zioimpl.CommonAbstractions.Repository.BaseRepository
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
  def insert(dbo: AirportDbo): RIO[AirportRepository, Long] =
    ZIO.serviceWithZIO[AirportRepository](_.insert(dbo))

  def insert(seq: Seq[AirportDbo]): RIO[AirportRepository, Seq[Long]] =
    ZIO.serviceWithZIO[AirportRepository](_.insertSeq(seq))

  def update(dbo: AirportDbo): RIO[AirportRepository, Int] =
    ZIO.serviceWithZIO[AirportRepository](_.update(dbo))

  /** Deletes an Aiport by its iata code
    * @param iataCode
    *   airport code
    * @return
    *   number of deleted rows
    */
  def deleteByIataCode(iataCode: String): RIO[AirportRepository, Int] =
    ZIO.serviceWithZIO[AirportRepository](_.deleteByIataCode(iataCode))

  def findByIataCode(code: String): RIO[AirportRepository, Option[AirportDbo]] =
    ZIO.serviceWithZIO[AirportRepository](_.findByIataCode(code))

  def findByIcaoCode(code: String): RIO[AirportRepository, Option[AirportDbo]] =
    ZIO.serviceWithZIO[AirportRepository](_.findByIcaoCode(code))

  def findByCountryCode(code: String): RIO[AirportRepository, Seq[AirportDbo]] =
    ZIO.serviceWithZIO[AirportRepository](_.findByCountryCode(code))

  def findByName(name: String): RIO[AirportRepository, Seq[AirportDbo]] =
    ZIO.serviceWithZIO[AirportRepository](_.findByName(name))

  def count(): RIO[AirportRepository, Int] =
    ZIO.serviceWithZIO[AirportRepository](_.count())

}
