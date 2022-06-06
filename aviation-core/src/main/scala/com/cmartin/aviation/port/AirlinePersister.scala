package com.cmartin.aviation.port

import com.cmartin.aviation.domain.Model._
import zio.{IO, ZIO}

trait AirlinePersister {
  def insert(airline: Airline): IO[ServiceError, Long]
  def existsByCode(code: IataCode): IO[ServiceError, Boolean]
  def findByCode(code: IataCode): IO[ServiceError, Option[Airline]]
  def findByCountry(code: CountryCode): IO[ServiceError, Seq[Airline]]
  def update(airline: Airline): IO[ServiceError, Int]
  def delete(code: IataCode): IO[ServiceError, Int]
}

object AirlinePersister {
  def insert(airline: Airline): ZIO[AirlinePersister, ServiceError, Long] =
    ZIO.serviceWithZIO[AirlinePersister](_.insert(airline))
  def existsByCode(code: IataCode): ZIO[AirlinePersister, ServiceError, Boolean] =
    ZIO.serviceWithZIO[AirlinePersister](_.existsByCode(code))
  def findByCode(code: IataCode): ZIO[AirlinePersister, ServiceError, Option[Airline]] =
    ZIO.serviceWithZIO[AirlinePersister](_.findByCode(code))

  def findByCountry(code: CountryCode): ZIO[AirlinePersister, ServiceError, Seq[Airline]] =
    ZIO.serviceWithZIO[AirlinePersister](_.findByCountry(code))
  def update(airline: Airline): ZIO[AirlinePersister, ServiceError, Int] =
    ZIO.serviceWithZIO[AirlinePersister](_.update(airline))
  def delete(code: IataCode): ZIO[AirlinePersister, ServiceError, Int] =
    ZIO.serviceWithZIO[AirlinePersister](_.delete(code))

}
