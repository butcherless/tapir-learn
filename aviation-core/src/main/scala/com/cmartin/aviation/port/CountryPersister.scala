package com.cmartin.aviation.port

import com.cmartin.aviation.domain.Model._
import zio.IO
import zio.ZIO

trait CountryPersister {
  def insert(country: Country): IO[ServiceError, Long]
  def existsByCode(code: CountryCode): IO[ServiceError, Boolean]
  def findByCode(code: CountryCode): IO[ServiceError, Option[Country]]
  def update(country: Country): IO[ServiceError, Int]
  def delete(code: CountryCode): IO[ServiceError, Int]
}

object CountryPersister {
  def insert(country: Country): ZIO[CountryPersister, ServiceError, Long] =
    ZIO.serviceWithZIO[CountryPersister](_.insert(country))

  def existsByCode(code: CountryCode): ZIO[CountryPersister, ServiceError, Boolean] =
    ZIO.serviceWithZIO[CountryPersister](_.existsByCode(code))

  def findByCode(code: CountryCode): ZIO[CountryPersister, ServiceError, Option[Country]] =
    ZIO.serviceWithZIO[CountryPersister](_.findByCode(code))

  def update(country: Country): ZIO[CountryPersister, ServiceError, Int] =
    ZIO.serviceWithZIO[CountryPersister](_.update(country))

  def delete(code: CountryCode): ZIO[CountryPersister, ServiceError, Int] =
    ZIO.serviceWithZIO[CountryPersister](_.delete(code))

}
