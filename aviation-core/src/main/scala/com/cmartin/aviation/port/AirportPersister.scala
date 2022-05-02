package com.cmartin.aviation.port

import com.cmartin.aviation.domain.Model._
import zio.{IO, ZIO}

trait AirportPersister {
  def insert(airport: Airport): IO[ServiceError, Long]
  def existsByCode(code: IataCode): IO[ServiceError, Boolean]
  def findByCode(code: IataCode): IO[ServiceError, Option[Airport]]
  def update(airport: Airport): IO[ServiceError, Int]
  def delete(code: IataCode): IO[ServiceError, Int]
}

object AirportPersister {
  def insert(airport: Airport): ZIO[AirportPersister, ServiceError, Long] =
    ZIO.serviceWithZIO[AirportPersister](_.insert(airport))

  def existsByCode(code: IataCode): ZIO[AirportPersister, ServiceError, Boolean] =
    ZIO.serviceWithZIO[AirportPersister](_.existsByCode(code))

  def findByCode(code: IataCode): ZIO[AirportPersister, ServiceError, Option[Airport]] =
    ZIO.serviceWithZIO[AirportPersister](_.findByCode(code))

  def update(airport: Airport): ZIO[AirportPersister, ServiceError, Int] =
    ZIO.serviceWithZIO[AirportPersister](_.update(airport))

  def delete(code: IataCode): ZIO[AirportPersister, ServiceError, Int] =
    ZIO.serviceWithZIO[AirportPersister](_.delete(code))

}
