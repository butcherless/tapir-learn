package com.cmartin.aviation.port

import com.cmartin.aviation.Commons.RepositoryResponse
import com.cmartin.aviation.domain.Model._
import zio._

trait CountryCrudRepository {
  def create(country: Country): IO[ServiceError, Long]
  def findByCode(code: String): IO[ServiceError, Option[Country]]
}

object CountryCrudRepository {
  def create(country: Country): ZIO[Has[CountryCrudRepository], ServiceError, Long] =
    ZIO.serviceWith[CountryCrudRepository](_.create(country))

  def findByCode(code: String): ZIO[Has[CountryCrudRepository], ServiceError, Option[Country]] =
    ZIO.serviceWith[CountryCrudRepository](_.findByCode(code))
}
