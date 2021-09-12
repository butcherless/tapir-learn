package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Model.CountryDbo
import zio.IO

trait CountryRepository {

  def insert(dbo: CountryDbo): IO[Throwable, Long]
  def findByCode(code: String): IO[Throwable, Option[CountryDbo]]
  def update(countryDbo: CountryDbo): IO[Throwable, Int]
  def delete(code: String): IO[Throwable, Int]
  def count(): IO[Throwable, Int]

}
