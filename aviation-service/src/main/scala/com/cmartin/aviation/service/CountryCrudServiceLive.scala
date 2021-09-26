package com.cmartin.aviation.service

import com.cmartin.aviation.Commons.ServiceResponse
import com.cmartin.aviation.domain.Model.Country
import com.cmartin.aviation.domain.Model.CountryCode
import com.cmartin.aviation.domain.Model.MissingEntityError
import com.cmartin.aviation.domain.Model.ServiceError
import com.cmartin.aviation.port.CountryCrudRepository
import com.cmartin.aviation.port.CountryService
import zio.IO
import zio.logging._
import zio._

import CountryCrudServiceLive._

case class CountryCrudServiceLive(logging: Logging, countryRepository: CountryCrudRepository)
    extends CountryService {

  override def create(country: Country): ServiceResponse[Country] = {
    val program = for {
      _ <- log.debug(s"create: $country")
      _ <- countryRepository.create(country)
    } yield country

    program.provide(logging)
  }

  override def findByCode(code: CountryCode): ServiceResponse[Country] = {

    val program = for {
      //_ <- log.debug(s"findByCode: $code")
      option <- countryRepository.findByCode(code)
      country <- manageNotFound(option)(s"No country found for code: $code")
    } yield country

    program
  }

  override def update(country: Country): ServiceResponse[Country] = {
    val program = for {
      //_ <- log.debug(s"update: $country")
      _ <- UIO("")
    } yield Country(CountryCode("es"), "Spain")

    program
  }

  override def deleteByCode(code: CountryCode): ServiceResponse[Int] = {
    val program = for {
      //_ <- log.debug(s"deleteByCode: $code")
      _ <- UIO("")
    } yield 1

    program
  }

}

object CountryCrudServiceLive {
  val layer: URLayer[Has[Logging] with Has[CountryCrudRepository], Has[CountryCrudServiceLive]] =
    (CountryCrudServiceLive(_, _)).toLayer

  /*
  def apply(countryRepository: CountryRepository): CountryCrudService =
    new CountryCrudService(countryRepository: CountryRepository)
   */

  def manageNotFound[A](o: Option[A])(message: String): IO[ServiceError, A] = {
    o.fold[IO[ServiceError, A]](
      IO.fail(MissingEntityError(message))
    )(a => IO.succeed(a))
  }

}
