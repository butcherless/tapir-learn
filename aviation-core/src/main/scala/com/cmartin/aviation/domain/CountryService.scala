package com.cmartin.aviation.domain

import com.cmartin.aviation.domain.Model._
import zio.IO

trait CountryService {
  def create(country: Country): IO[ServiceError, Country]
  def findByCode(code: CountryCode): IO[ServiceError, Country]
  def update(country: Country): IO[ServiceError, Country]
  def deleteByCode(code: CountryCode): IO[ServiceError, Unit]
}
