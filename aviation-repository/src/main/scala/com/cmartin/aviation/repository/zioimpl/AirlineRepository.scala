package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Model.AirlineDbo
import zio.IO
import com.cmartin.aviation.repository.BaseRepository

trait AirlineRepository
    extends BaseRepository[AirlineDbo] {

  def findByName(name: String): IO[Throwable, Seq[AirlineDbo]]
  def findByIataCode(code: String): IO[Throwable, Option[AirlineDbo]]
  def findByIcaoCode(code: String): IO[Throwable, Option[AirlineDbo]]
  def findByCountryCode(code: String): IO[Throwable, Seq[AirlineDbo]]
  def deleteByIataCode(code: String): IO[Throwable, Int]
}
