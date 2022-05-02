package com.cmartin.aviation.port

import com.cmartin.aviation.domain.Model._
import zio._

trait CountryCrudRepository {
  def create(country: Country): IO[ServiceError, Long]
  def findByCode(code: String): IO[ServiceError, Option[Country]]
}

object CountryCrudRepository {
  def create(country: Country): ZIO[CountryCrudRepository, ServiceError, Long] =
    ZIO.serviceWithZIO[CountryCrudRepository](_.create(country))

  def findByCode(code: String): ZIO[CountryCrudRepository, ServiceError, Option[Country]] =
    ZIO.serviceWithZIO[CountryCrudRepository](_.findByCode(code))
}
