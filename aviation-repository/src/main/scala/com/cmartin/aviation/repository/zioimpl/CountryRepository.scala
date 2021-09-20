package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Model.CountryDbo
import zio.IO

trait CountryRepository
    extends BaseRepository[CountryDbo] {

  def findByCode(code: String): IO[Throwable, Option[CountryDbo]]
  def delete(code: String): IO[Throwable, Int]

}
