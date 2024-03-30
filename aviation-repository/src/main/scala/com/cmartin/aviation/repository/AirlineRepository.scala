package com.cmartin.aviation.repository

import com.cmartin.aviation.repository.Model.AirlineDbo
import com.cmartin.aviation.repository.zioimpl.CommonAbstractions.Repository.BaseRepository
import zio.{RIO, Task, ZIO}

trait AirlineRepository
    extends BaseRepository[AirlineDbo] {

  /** Retrieves the sequence of airlines whose name matches the supplied
    * parameter
    *
    * @param name
    *   airline name
    * @return
    *   airline sequence
    */
  def findByName(name: String): Task[Seq[AirlineDbo]]

  /** Retrieves the airline option whose iata code matches the supplied
    * parameter
    *
    * @param code
    *   iata code
    * @return
    *   the airline option
    */
  def findByIataCode(code: String): Task[Option[AirlineDbo]]

  /** Retrieves the airline option whose icao code matches the supplied
    * parameter
    *
    * @param code
    *   icao code
    * @return
    *   airline option
    */
  def findByIcaoCode(code: String): Task[Option[AirlineDbo]]

  /** Retrieves the sequence of airlines whose country code matches the supplied
    * parameter
    *
    * @param code
    *   country code
    * @return
    *   airline sequence
    */
  def findByCountryCode(code: String): Task[Seq[AirlineDbo]]

  /** Deletes an Airline by its IATA code
    * @param code
    *   airline IATA code
    * @return
    *   number of deleted rows
    */
  def deleteByIataCode(code: String): Task[Int]
}

object AirlineRepository {
  def insert(dbo: AirlineDbo): RIO[AirlineRepository, Long] =
    ZIO.serviceWithZIO[AirlineRepository](_.insert(dbo))

  def insert(seq: Seq[AirlineDbo]): RIO[AirlineRepository, Seq[Long]] =
    ZIO.serviceWithZIO[AirlineRepository](_.insertSeq(seq))

  def update(dbo: AirlineDbo): RIO[AirlineRepository, Int] =
    ZIO.serviceWithZIO[AirlineRepository](_.update(dbo))

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
