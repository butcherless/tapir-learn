package com.cmartin.aviation.port

import com.cmartin.aviation.domain.Model._
import zio.{Has, IO, ZIO}

trait CountryPersister {
  def insert(country: Country): IO[ServiceError, Long]
  def existsByCode(code: CountryCode): IO[ServiceError, Boolean]
  def findByCode(code: CountryCode): IO[ServiceError, Option[Country]]
  def update(country: Country): IO[ServiceError, Int]
  def delete(code: CountryCode): IO[ServiceError, Int]
}

object CountryPersister {
  def insert(country: Country): ZIO[Has[CountryPersister], ServiceError, Long] =
    ZIO.serviceWith[CountryPersister](_.insert(country))

  def existsByCode(code: CountryCode): ZIO[Has[CountryPersister], ServiceError, Boolean] =
    ZIO.serviceWith[CountryPersister](_.existsByCode(code))

  def findByCode(code: CountryCode): ZIO[Has[CountryPersister], ServiceError, Option[Country]] =
    ZIO.serviceWith[CountryPersister](_.findByCode(code))

  def update(country: Country): ZIO[Has[CountryPersister], ServiceError, Int] =
    ZIO.serviceWith[CountryPersister](_.update(country))

  def delete(code: CountryCode): ZIO[Has[CountryPersister], ServiceError, Int] =
    ZIO.serviceWith[CountryPersister](_.delete(code))

}
