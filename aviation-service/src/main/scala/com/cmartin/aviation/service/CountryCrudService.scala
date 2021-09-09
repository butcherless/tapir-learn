package com.cmartin.aviation.service

import com.cmartin.aviation.Commons.ServiceResponse
import com.cmartin.aviation.domain.Model.{Country, CountryCode, MissingEntityError, ServiceError}
import com.cmartin.aviation.port.{CountryRepository, CountryService}
import zio.IO
import zio.logging._
import CountryCrudService._

class CountryCrudService(countryRepository: CountryRepository) extends CountryService {

  override def create(country: Country): ServiceResponse[Country] = {
    val program = for {
      _ <- log.debug(s"create: $country")
      _ <- countryRepository.create(country)
    } yield country

    program
  }

  override def findByCode(code: CountryCode): ServiceResponse[Country] = {

    val program = for {
      _ <- log.debug(s"findByCode: $code")
      option <- countryRepository.findByCode(code)
      country <- manageNotFound(option)(s"No country found for code: $code")
    } yield country

    program
  }

  override def update(country: Country): ServiceResponse[Country] = {
    val program = for {
      _ <- log.debug(s"update: $country")
    } yield Country(CountryCode("es"), "Spain")

    program
  }

  override def deleteByCode(code: CountryCode): ServiceResponse[Int] = {
    val program = for {
      _ <- log.debug(s"deleteByCode: $code")
    } yield 1

    program
  }

}

object CountryCrudService {
  def apply(countryRepository: CountryRepository): CountryCrudService =
    new CountryCrudService(countryRepository: CountryRepository)

  def manageNotFound[A](o: Option[A])(message: String): IO[ServiceError, A] = {
    o.fold[IO[ServiceError, A]](
      IO.fail(MissingEntityError(message))
    )(a => IO.succeed(a))
  }

}
