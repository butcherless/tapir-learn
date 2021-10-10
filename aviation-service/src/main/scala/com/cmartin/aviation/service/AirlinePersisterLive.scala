package com.cmartin.aviation.service

import com.cmartin.aviation.domain.Model._
import com.cmartin.aviation.port.AirlinePersister
import com.cmartin.aviation.repository.AirlineRepository
import com.cmartin.aviation.repository.CountryRepository
import com.cmartin.aviation.repository.Model._
import com.cmartin.aviation.repository.zioimpl.Mappers._
import com.cmartin.aviation.repository.zioimpl.common._
import zio._
import zio.logging._

final case class AirlinePersisterLive(
    logging: Logging,
    countryRepository: CountryRepository,
    airlineRepository: AirlineRepository
) extends AirlinePersister {

  import AirlinePersisterLive._

  override def insert(airline: Airline): IO[ServiceError, Long] = {
    val program = for {
      _ <- log.debug(s"insert: $airline")
      option <- countryRepository.findByCode(airline.country.code)
      country <- manageNotFound(option)(s"No country found for code: ${airline.country.code}")
      id <- airlineRepository.insert(airline.toDbo(country.id.get)) // safe access by primary key
    } yield id

    program
      .provide(logging)
      .mapError(manageError)
  }

  override def existsByCode(code: IataCode): IO[ServiceError, Boolean] = {
    val program = for {
      _ <- log.debug(s"existsByCode: $code")
      dbo <- airlineRepository.findByIataCode(code)
    } yield dbo.isDefined

    program
      .provide(logging)
      .mapError(manageError)
  }

  override def findByCode(code: IataCode): IO[ServiceError, Option[Airline]] = {
    val program = for {
      _ <- log.debug(s"findByCode: $code")
      airlineOpt <- airlineRepository.findByIataCode(code)
      airlineWithCountryOpt <- findCountryAndToDomain(airlineOpt)
    } yield airlineWithCountryOpt

    program
      .provide(logging)
      .mapError(manageError)
  }

  private def findCountryAndToDomain(dboOpt: Option[AirlineDbo]): Task[Option[Airline]] =
    dboOpt match {
      case Some(dbo) =>
        countryRepository.find(dbo.countryId)
          .map(opt => Some(dbo.toDomain(opt.get))) // safe access by foreign key
      case None =>
        Task.none
    }

  override def findByCountry(code: CountryCode): IO[ServiceError, Seq[Airline]] = {
    val program = for {
      _ <- log.debug(s"findByCountry: $code")
      dbos <- airlineRepository.findByCountryCode(code)
      countryOpt <- countryRepository.findByCode(code)
    } yield dbos.map(_.toDomain(countryOpt.get)) // safe access by foreign key

    program
      .provide(logging)
      .mapError(manageError)
  }

  override def update(airline: Airline): IO[ServiceError, Int] = {
    val program = for {
      _ <- log.debug(s"update: $airline")
      countryOpt <- countryRepository.findByCode(airline.country.code)
      country <- manageNotFound(countryOpt)(s"No country found for code: ${airline.country.code}")
      airlineOpt <- airlineRepository.findByIataCode(airline.iataCode)
      found <- manageNotFound(airlineOpt)(s"No airline found for code: ${airline.iataCode}")
      updated <- buildDbo(airline, found.id, country.id)
      count <- airlineRepository.update(updated)
    } yield count

    program
      .provide(logging)
      .mapError(manageError)
  }

  override def delete(code: IataCode): IO[ServiceError, Int] = {
    val program = for {
      _ <- log.debug(s"delete: $code")
      dbo <- airlineRepository.deleteByIataCode(code)
    } yield dbo

    program
      .provide(logging)
      .mapError(manageError)
  }
}

object AirlinePersisterLive {
  val layer: URLayer[Has[Logging] with Has[CountryRepository] with Has[AirlineRepository], Has[AirlinePersister]] =
    (AirlinePersisterLive(_, _, _)).toLayer

  def buildDbo(airline: Airline, id: Option[Long], countryId: Option[Long]): UIO[AirlineDbo] =
    IO.succeed(AirlineDbo(
      name = airline.name,
      iataCode = airline.iataCode,
      icaoCode = airline.icaoCode,
      foundationDate = airline.foundationDate,
      countryId = countryId.get, // safe access by foreign key
      id = id
    ))
}
