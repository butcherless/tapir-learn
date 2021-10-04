package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.domain.Model._
import com.cmartin.aviation.port.CountryPersister
import com.cmartin.aviation.repository.CountryRepository
import com.cmartin.aviation.repository.zioimpl.Mappers._
import zio._
import zio.logging._

case class CountryPersisterLive(logging: Logging, countryRepository: CountryRepository)
    extends CountryPersister {

  override def existsByCode(code: CountryCode): IO[ServiceError, Boolean] = {
    val program = for {
      _ <- log.debug(s"existsByCode: $code")
      dbo <- countryRepository.findByCode(code)
    } yield dbo.isDefined

    program
      .provide(logging)
      .mapError {
        case e @ _ => UnexpectedServiceError(e.getMessage())
      }
  }

  override def insert(country: Country): IO[ServiceError, Long] = {
    val program = for {
      _ <- log.debug(s"insert: $country")
      id <- countryRepository.insert(country.toDbo)
    } yield id

    //TODO manageError function
    program
      .provide(logging)
      .mapError {
        case e @ _ => UnexpectedServiceError(e.getMessage())
      }
  }

  override def findByCode(code: CountryCode): IO[ServiceError, Option[Country]] = {
    val program = for {
      _ <- log.debug(s"findByCode: $code")
      dbo <- countryRepository.findByCode(code)
    } yield dbo.toDomain

    program
      .provide(logging)
      .mapError {
        case e @ _ => UnexpectedServiceError(e.getMessage())
      }
  }

  override def update(country: Country): IO[ServiceError, Int] = ???

  override def delete(code: CountryCode): IO[ServiceError, Int] = {
    val program = for {
      _ <- log.debug(s"delete: $code")
      dbo <- countryRepository.delete(code)
    } yield dbo

    program
      .provide(logging)
      .mapError {
        case e @ _ => UnexpectedServiceError(e.getMessage())
      }
  }

}

object CountryPersisterLive {
  val layer: URLayer[Has[Logging] with Has[CountryRepository], Has[CountryPersister]] =
    (CountryPersisterLive(_, _)).toLayer
}
