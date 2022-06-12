package com.cmartin.aviation

import com.cmartin.aviation.domain.Model.{Country, CountryCode}
import zio.{IO, ZIO}

object ServiceLayer {

  object Domain {
    trait ServiceError
    case class CountryNotFound(code: String)      extends ServiceError
    case class DuplicateEntityError(text: String) extends ServiceError
    case class DefaultError(text: String)         extends ServiceError
  }

  object CountryService {
    import Domain._

    def searchByCode(code: String): IO[ServiceError, Country] =
      ZIO.ifZIO(ZIO.succeed(code == "es"))(
        ZIO.succeed(Country(CountryCode(code), "Spain")),
        ZIO.fail(CountryNotFound(code))
      )

    def create(country: Country): IO[ServiceError, Unit] =
      country.code match {
        case CountryCode("es") => ZIO.unit
        case CountryCode("pt") => ZIO.fail(DuplicateEntityError(s"${country}"))
        case _                 => ZIO.fail(DefaultError(s"service error procesing: $country"))
      }

  }
}
