package com.cmartin.aviation.service

import com.cmartin.aviation.domain.Model._
import com.cmartin.aviation.port.AirportPersister
import com.cmartin.aviation.repository.AirportRepository
import com.cmartin.aviation.repository.CountryRepository
import com.cmartin.aviation.repository.Model._
import com.cmartin.aviation.repository.zioimpl.Mappers._
import com.cmartin.aviation.repository.zioimpl.common._
import zio._
import zio.logging._

case class AirportPersisterLive(
    logging: Logging,
    countryRepository: CountryRepository,
    airportRepository: AirportRepository
) extends AirportPersister {

  import AirportPersisterLive.buildDbo
  override def insert(airport: Airport): IO[ServiceError, Long] = {
    val program = for {
      _ <- log.debug(s"insert: $airport")
      option <- countryRepository.findByCode(airport.country.code)
      country <- manageNotFound(option)(s"No country found for code: ${airport.country.code}")
      id <- airportRepository.insert(airport.toDbo(country.id.get)) // safe access by primary key
    } yield id

    program
      .provide(logging)
      .mapError(manageError)
  }

  override def existsByCode(code: IataCode): IO[ServiceError, Boolean] = {
    val program = for {
      _ <- log.debug(s"existsByCode: $code")
      dbo <- airportRepository.findByIataCode(code)
    } yield dbo.isDefined

    program
      .provide(logging)
      .mapError(manageError)
  }

  override def findByCode(code: IataCode): IO[ServiceError, Option[Airport]] = {
    val program = for {
      _ <- log.debug(s"findByCode: $code")
      airportOpt <- airportRepository.findByIataCode(code)
      airportWithCountryOpt <- findCountryAndToDomain(airportOpt)
    } yield airportWithCountryOpt

    program
      .provide(logging)
      .mapError(manageError)
  }

  private def findCountryAndToDomain(dboOpt: Option[AirportDbo]): Task[Option[Airport]] =
    dboOpt match {
      case Some(dbo) =>
        countryRepository.find(dbo.countryId)
          .map(opt => Some(dbo.toDomain(opt.get))) // safe access by foreign key
      case None =>
        Task.none
    }

  override def update(airport: Airport): IO[ServiceError, Int] = {
    val program = for {
      _ <- log.debug(s"update: $airport")
      countryOpt <- countryRepository.findByCode(airport.country.code)
      country <- manageNotFound(countryOpt)(s"No country found for code: ${airport.country.code}")
      airportOpt <- airportRepository.findByIataCode(airport.iataCode)
      found <- manageNotFound(airportOpt)(s"No airport found for code: ${airport.iataCode}")
      updated <- buildDbo(airport, found.id, country.id)
      count <- airportRepository.update(updated)
    } yield count

    program
      .provide(logging)
      .mapError(manageError)
  }

  override def delete(code: IataCode): IO[ServiceError, Int] = {
    val program = for {
      _ <- log.debug(s"delete: $code")
      dbo <- airportRepository.deleteByIataCode(code)
    } yield dbo

    program
      .provide(logging)
      .mapError(manageError)
  }
}

object AirportPersisterLive {

  val layer: URLayer[Has[Logging] with Has[CountryRepository] with Has[AirportRepository], Has[AirportPersister]] =
    (AirportPersisterLive(_, _, _)).toLayer

  def buildDbo(airport: Airport, id: Option[Long], countryId: Option[Long]): UIO[AirportDbo] =
    IO.succeed(AirportDbo(
      name = airport.name,
      iataCode = airport.iataCode,
      icaoCode = airport.icaoCode,
      countryId = countryId.get, // safe access by foreign key
      id = id
    ))

}
