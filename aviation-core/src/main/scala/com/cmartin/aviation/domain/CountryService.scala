package com.cmartin.aviation.domain

import com.cmartin.aviation.domain.Model._
import zio.IO

trait CountryService {
  def findByCode(code: CountryCode): IO[ServiceError, Country] = ???
}
