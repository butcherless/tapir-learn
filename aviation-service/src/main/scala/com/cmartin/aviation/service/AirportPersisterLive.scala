package com.cmartin.aviation.service

import com.cmartin.aviation.domain.Model._
import com.cmartin.aviation.port.AirportPersister
import com.cmartin.aviation.repository.{AirportRepository, CountryRepository}
import com.cmartin.aviation.repository.Model._
import com.cmartin.aviation.repository.zioimpl.Mappers._
import com.cmartin.aviation.repository.zioimpl.common._
import zio._

case class AirportPersisterLive(
    countryRepository: CountryRepository,
    airportRepository: AirportRepository
) extends AirportPersister {

  import AirportPersisterLive.buildDbo
  override def insert(airport: Airport): IO[ServiceError, Long] = {
    val program = for {
      _ <- ZIO.logDebug(s"insert: $airport")
      option <- countryRepository.findByCode(airport.country.code)
      country <- manageNotFound(option)(s"No country found for code: ${airport.country.code}")
      id <- airportRepository.insert(airport.toDbo(country.id)) // safe access by primary key
    } yield id

    program
      .mapError(manageError)
  }

  override def existsByCode(code: IataCode): IO[ServiceError, Boolean] = {
    val program = for {
      _ <- ZIO.logDebug(s"existsByCode: $code")
      dbo <- airportRepository.findByIataCode(code)
    } yield dbo.isDefined

    program
      .mapError(manageError)
  }

  override def findByCode(code: IataCode): IO[ServiceError, Option[Airport]] = {
    val program = for {
      _ <- ZIO.logDebug(s"findByCode: $code")
      airportOpt <- airportRepository.findByIataCode(code)
      airportWithCountryOpt <- findCountryAndToDomain(airportOpt)
    } yield airportWithCountryOpt

    program
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
      _ <- ZIO.logDebug(s"update: $airport")
      countryOpt <- countryRepository.findByCode(airport.country.code)
      country <- manageNotFound(countryOpt)(s"No country found for code: ${airport.country.code}")
      airportOpt <- airportRepository.findByIataCode(airport.iataCode)
      found <- manageNotFound(airportOpt)(s"No airport found for code: ${airport.iataCode}")
      updated <- buildDbo(airport, found.id, country.id)
      count <- airportRepository.update(updated)
    } yield count

    program
      .mapError(manageError)
  }

  override def delete(code: IataCode): IO[ServiceError, Int] = {
    val program = for {
      _ <- ZIO.logDebug(s"delete: $code")
      dbo <- airportRepository.deleteByIataCode(code)
    } yield dbo

    program
      .mapError(manageError)
  }
}

object AirportPersisterLive {

  val layer: URLayer[CountryRepository with AirportRepository, AirportPersister] =
    ZLayer {
      for {
        c <- ZIO.service[CountryRepository]
        a <- ZIO.service[AirportRepository]
      } yield AirportPersisterLive(c, a)
    }

  def buildDbo(airport: Airport, id: Long, countryId: Long): UIO[AirportDbo] =
    ZIO.succeed(AirportDbo(
      name = airport.name,
      iataCode = airport.iataCode,
      icaoCode = airport.icaoCode,
      countryId = countryId, // safe access by foreign key
      id = id
    ))

}
