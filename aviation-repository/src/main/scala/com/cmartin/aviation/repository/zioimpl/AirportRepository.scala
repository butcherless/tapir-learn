package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Model.AirportDbo
import zio.IO
import com.cmartin.aviation.repository.BaseRepository

trait AirportRepository
    extends BaseRepository[AirportDbo] {

  def deleteByIataCode(code: String): IO[Throwable, Int]
  def findByIataCode(code: String): IO[Throwable, Option[AirportDbo]]
  def findByIcaoCode(code: String): IO[Throwable, Option[AirportDbo]]
  def findByCountryCode(code: String): IO[Throwable, Seq[AirportDbo]]
  def findByName(name: String): IO[Throwable, Seq[AirportDbo]]

}
