package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Model.AirportDbo
import zio.IO

trait AirportRepository {

  def insert(dbo: AirportDbo): IO[Throwable, Long]
  def update(dbo: AirportDbo): IO[Throwable, Int]
  def delete(code: String): IO[Throwable, Int]
  def findByIataCode(code: String): IO[Throwable, Option[AirportDbo]]
  def findByIcaoCode(code: String): IO[Throwable, Option[AirportDbo]]
  def findByCountryCode(code: String): IO[Throwable, Seq[AirportDbo]]
  def findByName(name: String): IO[Throwable, Seq[AirportDbo]]
  def count(): IO[Throwable, Int]

}
