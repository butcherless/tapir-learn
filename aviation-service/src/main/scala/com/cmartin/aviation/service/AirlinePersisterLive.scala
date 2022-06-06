package com.cmartin.aviation.service

import com.cmartin.aviation.domain.Model._
import com.cmartin.aviation.port.AirlinePersister
import com.cmartin.aviation.repository.Model._
import com.cmartin.aviation.repository.zioimpl.Mappers._
import com.cmartin.aviation.repository.zioimpl.common._
import com.cmartin.aviation.repository.{AirlineRepository, CountryRepository}
import zio._

final case class AirlinePersisterLive(
    countryRepository: CountryRepository,
    airlineRepository: AirlineRepository
) extends AirlinePersister {

  import AirlinePersisterLive.buildDbo

  override def insert(airline: Airline): IO[ServiceError, Long] = {
    val program = for {
      _       <- ZIO.logDebug(s"insert: $airline")
      option  <- countryRepository.findByCode(airline.country.code)
      country <- manageNotFound(option)(s"No country found for code: ${airline.country.code}")
      id      <- airlineRepository.insert(airline.toDbo(country.id)) // safe access by primary key
    } yield id

    program
      .mapError(manageError)
  }

  override def existsByCode(code: IataCode): IO[ServiceError, Boolean] = {
    val program = for {
      _   <- ZIO.logDebug(s"existsByCode: $code")
      dbo <- airlineRepository.findByIataCode(code)
    } yield dbo.isDefined

    program
      .mapError(manageError)
  }

  override def findByCode(code: IataCode): IO[ServiceError, Option[Airline]] = {
    val program = for {
      _                     <- ZIO.logDebug(s"findByCode: $code")
      airlineOpt            <- airlineRepository.findByIataCode(code)
      airlineWithCountryOpt <- findCountryAndToDomain(airlineOpt)
    } yield airlineWithCountryOpt

    program
      .mapError(manageError)
  }

  private def findCountryAndToDomain(dboOpt: Option[AirlineDbo]): Task[Option[Airline]] =
    dboOpt match {
      case Some(dbo) =>
        countryRepository.find(dbo.countryId)
          .map(opt => Some(dbo.toDomain(opt.get))) // safe access by foreign key
      case None      =>
        ZIO.none
    }

  override def findByCountry(code: CountryCode): IO[ServiceError, Seq[Airline]] = {
    val program = for {
      _          <- ZIO.logDebug(s"findByCountry: $code")
      dbos       <- airlineRepository.findByCountryCode(code)
      countryOpt <- countryRepository.findByCode(code)
    } yield dbos.map(_.toDomain(countryOpt.get)) // safe access by foreign key

    program
      .mapError(manageError)
  }

  override def update(airline: Airline): IO[ServiceError, Int] = {
    val program = for {
      _          <- ZIO.logDebug(s"update: $airline")
      countryOpt <- countryRepository.findByCode(airline.country.code)
      country    <- manageNotFound(countryOpt)(s"No country found for code: ${airline.country.code}")
      airlineOpt <- airlineRepository.findByIataCode(airline.iataCode)
      found      <- manageNotFound(airlineOpt)(s"No airline found for code: ${airline.iataCode}")
      updated    <- buildDbo(airline, found.id, country.id)
      count      <- airlineRepository.update(updated)
    } yield count

    program
      .mapError(manageError)
  }

  override def delete(code: IataCode): IO[ServiceError, Int] = {
    val program = for {
      _   <- ZIO.logDebug(s"delete: $code")
      dbo <- airlineRepository.deleteByIataCode(code)
    } yield dbo

    program
      .mapError(manageError)
  }
}

object AirlinePersisterLive {
  val layer: URLayer[CountryRepository with AirlineRepository, AirlinePersister] =
    ZLayer {
      for {
        c <- ZIO.service[CountryRepository]
        a <- ZIO.service[AirlineRepository]
      } yield AirlinePersisterLive(c, a)
    }

  def buildDbo(airline: Airline, id: Long, countryId: Long): UIO[AirlineDbo] =
    ZIO.succeed(
      AirlineDbo(
        name = airline.name,
        iataCode = airline.iataCode,
        icaoCode = airline.icaoCode,
        foundationDate = airline.foundationDate,
        countryId = countryId, // safe access by foreign key
        id = id
      )
    )
}
