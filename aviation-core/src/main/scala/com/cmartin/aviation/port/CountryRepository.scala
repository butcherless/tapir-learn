package com.cmartin.aviation.port

import com.cmartin.aviation.Commons.RepositoryResponse
import com.cmartin.aviation.domain.Model._
import zio.IO

trait CountryRepository {
  def create(country: Country): IO[ServiceError, Long]
  def findByCode(code: String): IO[ServiceError, Option[Country]]
}
