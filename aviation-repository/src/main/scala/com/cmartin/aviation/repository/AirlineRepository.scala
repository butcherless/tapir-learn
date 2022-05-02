package com.cmartin.aviation.repository

import com.cmartin.aviation.repository.Model.AirlineDbo
import com.cmartin.aviation.repository.zioimpl.CommonAbstractions.Repository.BaseRepository
import zio.{RIO, Task, ZIO}

trait AirlineRepository
    extends BaseRepository[AirlineDbo] {

  def findByName(name: String): Task[Seq[AirlineDbo]]
  def findByIataCode(code: String): Task[Option[AirlineDbo]]
  def findByIcaoCode(code: String): Task[Option[AirlineDbo]]
  def findByCountryCode(code: String): Task[Seq[AirlineDbo]]
  def deleteByIataCode(code: String): Task[Int]
}

object AirlineRepository {
  def insert(dbo: AirlineDbo): RIO[AirlineRepository, Long] =
    ZIO.serviceWithZIO[AirlineRepository](_.insert(dbo))

  def insert(seq: Seq[AirlineDbo]): RIO[AirlineRepository, Seq[Long]] =
    ZIO.serviceWithZIO[AirlineRepository](_.insertSeq(seq))

  def update(dbo: AirlineDbo): RIO[AirlineRepository, Int] =
    ZIO.serviceWithZIO[AirlineRepository](_.update(dbo))

  /** Deletes an Airline by its iata code
    * @param iataCode
    *   airline code
    * @return
    *   number of deleted rows
    */
  def deleteByIataCode(iataCode: String): RIO[AirlineRepository, Int] =
    ZIO.serviceWithZIO[AirlineRepository](_.deleteByIataCode(iataCode))

  def findByIataCode(code: String): RIO[AirlineRepository, Option[AirlineDbo]] =
    ZIO.serviceWithZIO[AirlineRepository](_.findByIataCode(code))

  def findByIcaoCode(code: String): RIO[AirlineRepository, Option[AirlineDbo]] =
    ZIO.serviceWithZIO[AirlineRepository](_.findByIcaoCode(code))

  def findByCountryCode(code: String): RIO[AirlineRepository, Seq[AirlineDbo]] =
    ZIO.serviceWithZIO[AirlineRepository](_.findByCountryCode(code))

  def findByName(name: String): RIO[AirlineRepository, Seq[AirlineDbo]] =
    ZIO.serviceWithZIO[AirlineRepository](_.findByName(name))

  def count(): RIO[AirlineRepository, Int] =
    ZIO.serviceWithZIO[AirlineRepository](_.count())

}
