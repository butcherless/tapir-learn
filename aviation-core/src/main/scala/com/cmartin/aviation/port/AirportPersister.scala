package com.cmartin.aviation.port

import com.cmartin.aviation.domain.Model._
import zio.{Has, IO, ZIO}

trait AirportPersister {
  def insert(airport: Airport): IO[ServiceError, Long]
  def existsByCode(code: IataCode): IO[ServiceError, Boolean]
  def findByCode(code: IataCode): IO[ServiceError, Option[Airport]]
  def update(airport: Airport): IO[ServiceError, Int]
  def delete(code: IataCode): IO[ServiceError, Int]
}

object AirportPersister {
  def insert(airport: Airport): ZIO[Has[AirportPersister], ServiceError, Long] =
    ZIO.serviceWith[AirportPersister](_.insert(airport))

  def existsByCode(code: IataCode): ZIO[Has[AirportPersister], ServiceError, Boolean] =
    ZIO.serviceWith[AirportPersister](_.existsByCode(code))

  def findByCode(code: IataCode): ZIO[Has[AirportPersister], ServiceError, Option[Airport]] =
    ZIO.serviceWith[AirportPersister](_.findByCode(code))

  def update(airport: Airport): ZIO[Has[AirportPersister], ServiceError, Int] =
    ZIO.serviceWith[AirportPersister](_.update(airport))

  def delete(code: IataCode): ZIO[Has[AirportPersister], ServiceError, Int] =
    ZIO.serviceWith[AirportPersister](_.delete(code))

}
