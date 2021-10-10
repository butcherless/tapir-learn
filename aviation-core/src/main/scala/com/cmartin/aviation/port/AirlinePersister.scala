package com.cmartin.aviation.port

import com.cmartin.aviation.domain.Model._
import zio.Has
import zio.IO
import zio.ZIO

trait AirlinePersister {
  def insert(airline: Airline): IO[ServiceError, Long]
  def existsByCode(code: IataCode): IO[ServiceError, Boolean]
  def findByCode(code: IataCode): IO[ServiceError, Option[Airline]]
  def findByCountry(code: CountryCode): IO[ServiceError, Seq[Airline]]
  def update(airline: Airline): IO[ServiceError, Int]
  def delete(code: IataCode): IO[ServiceError, Int]
}

object AirlinePersister {
  def insert(airline: Airline): ZIO[Has[AirlinePersister], ServiceError, Long] =
    ZIO.serviceWith[AirlinePersister](_.insert(airline))
  def existsByCode(code: IataCode): ZIO[Has[AirlinePersister], ServiceError, Boolean] =
    ZIO.serviceWith[AirlinePersister](_.existsByCode(code))
  def findByCode(code: IataCode): ZIO[Has[AirlinePersister], ServiceError, Option[Airline]] =
    ZIO.serviceWith[AirlinePersister](_.findByCode(code))

  def findByCountry(code: CountryCode): ZIO[Has[AirlinePersister], ServiceError, Seq[Airline]] =
    ZIO.serviceWith[AirlinePersister](_.findByCountry(code))
  def update(airline: Airline): ZIO[Has[AirlinePersister], ServiceError, Int] =
    ZIO.serviceWith[AirlinePersister](_.update(airline))
  def delete(code: IataCode): ZIO[Has[AirlinePersister], ServiceError, Int] =
    ZIO.serviceWith[AirlinePersister](_.delete(code))

}
