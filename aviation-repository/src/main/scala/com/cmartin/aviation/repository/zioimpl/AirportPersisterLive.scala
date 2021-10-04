package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.domain.Model._
import com.cmartin.aviation.port.AirportPersister
import com.cmartin.aviation.port.CountryPersister
import com.cmartin.aviation.repository.AirportRepository
import com.cmartin.aviation.repository.CountryRepository
import com.cmartin.aviation.repository.Model._
import com.cmartin.aviation.repository.zioimpl.Mappers._
import zio._
import zio.logging._

case class AirportPersisterLive(
    logging: Logging,
    countryRepository: CountryRepository,
    airportRepository: AirportRepository
) extends AirportPersister {

  import AirportPersisterLive._

  override def insert(airport: Airport): IO[ServiceError, Long] = {
    val program = for {
      _ <- log.debug(s"insert: $airport")
      option <- countryRepository.findByCode(airport.country.code)
      country <- manageNotFound(option)(s"No country found for code: ${airport.country.code}")
      id <- airportRepository.insert(airport.toDbo(country.id.get)) // safe access by primary key
    } yield id
    //TODO manageError function
    program
      .provide(logging)
      .mapError {
        case e @ _ => UnexpectedServiceError(e.getMessage())
      }
  }

  override def existsByCode(code: IataCode): IO[ServiceError, Boolean] = {
    val program = for {
      _ <- log.debug(s"existsByCode: $code")
      dbo <- airportRepository.findByIataCode(code)
    } yield dbo.isDefined

    program
      .provide(logging)
      .mapError {
        case e @ _ => UnexpectedServiceError(e.getMessage())
      }
  }

  override def findByCode(code: IataCode): IO[ServiceError, Option[Airport]] = ???

  override def update(airport: Airport): IO[ServiceError, Int] = ???

  override def delete(code: IataCode): IO[ServiceError, Int] = {
    val program = for {
      _ <- log.debug(s"delete: $code")
      dbo <- airportRepository.deleteByIataCode(code)
    } yield dbo

    program
      .provide(logging)
      .mapError {
        case e @ _ => UnexpectedServiceError(e.getMessage())
      }

  }
}

object AirportPersisterLive {

  def manageNotFound[A](o: Option[A])(message: String): Task[A] = {
    o.fold[Task[A]](
      Task.fail(RepositoryException(message))
    )(a => Task.succeed(a))
  }

}
