package com.cmartin.learn.aviation.domain

import com.cmartin.learn.aviation.domain.Model._
import zio.IO

trait CountryService {
  def findByCode(code: CountryCode): IO[ServiceError, Country] = ???
}
