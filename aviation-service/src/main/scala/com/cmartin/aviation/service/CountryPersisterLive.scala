package com.cmartin.aviation.service

import com.cmartin.aviation.domain.Model._
import com.cmartin.aviation.port.CountryPersister
import com.cmartin.aviation.repository.CountryRepository
import com.cmartin.aviation.repository.Model._
import com.cmartin.aviation.repository.zioimpl.Mappers._
import com.cmartin.aviation.repository.zioimpl.common._
import zio._

case class CountryPersisterLive(
    countryRepository: CountryRepository
) extends CountryPersister {

  import CountryPersisterLive._

  override def existsByCode(code: CountryCode): IO[ServiceError, Boolean] = {
    val program = for {
      _ <- ZIO.logDebug(s"existsByCode: $code")
      dbo <- countryRepository.findByCode(code)
    } yield dbo.isDefined

    program
      .mapError(manageError)
  }

  override def insert(country: Country): IO[ServiceError, Long] = {
    val program = for {
      _ <- ZIO.logDebug(s"insert: $country")
      id <- countryRepository.insert(country.toDbo)
    } yield id

    program
      .mapError(manageError)
  }

  override def findByCode(code: CountryCode): IO[ServiceError, Option[Country]] = {
    val program = for {
      _ <- ZIO.logDebug(s"findByCode: $code")
      dbo <- countryRepository.findByCode(code)
    } yield dbo.toDomain

    program
      .mapError(manageError)
  }

  override def update(country: Country): IO[ServiceError, Int] = {
    val program = for {
      _ <- ZIO.logDebug(s"update: $country")
      countryOpt <- countryRepository.findByCode(country.code)
      found <- manageNotFound(countryOpt)(s"No country found for code: ${country.code}")
      updated <- buildDbo(country, found.id)
      count <- countryRepository.update(updated)
    } yield count

    program
      .mapError(manageError)
  }

  override def delete(code: CountryCode): IO[ServiceError, Int] = {
    val program = for {
      _ <- ZIO.logDebug(s"delete: $code")
      dbo <- countryRepository.delete(code)
    } yield dbo

    program
      .mapError(manageError)
  }

}

object CountryPersisterLive {
  val layer: URLayer[CountryRepository, CountryPersister] = {
    ZLayer.fromFunction(repo => CountryPersisterLive(repo))
  }

  def buildDbo(country: Country, id: Option[Long]): UIO[CountryDbo] =
    IO.succeed(CountryDbo(country.name, country.code, id))
}
