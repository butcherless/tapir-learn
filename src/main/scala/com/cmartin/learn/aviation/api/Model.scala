package com.cmartin.learn.aviation.api

import java.time.LocalDate
import zio.prelude.Subtype

object Model {

  object CountryCode extends Subtype[String] {
    type CountryCode = CountryCode.Type
  }

  //TODO move to domain
  sealed trait DomainError {
    val message: String
  }

  sealed trait ApiError extends DomainError
  sealed trait RestValidationError extends DomainError

  abstract class DescribedError extends RestValidationError {
    val message: String
  }

  case class InvalidCountryCode(message: String) extends DescribedError

  case class EmptyProperty(message: String) extends DescribedError
  case class InvalidCodeLength(message: String) extends DescribedError

  case class InvalidNameCharacters(message: String) extends DescribedError

  case class ValidationError(message: String, exception: Option[Throwable] = None) extends ApiError

  //TODO opaque Type, regex, constraints
  case class CountryView(
      code: String,
      name: String
  )

  case class AirportView(
      name: String,
      iataCode: String,
      icaoCode: String,
      airportCode: String
  )

  case class AirlineView(
      name: String,
      code: String,
      foundationDate: LocalDate,
      countryCode: String
  )

  /* ERROR MODEL */
  sealed trait OutputError {
    val code: String
    val message: String
  }

  case class BadRequestError(code: String, message: String) extends OutputError

  case class NotFoundError(code: String, message: String) extends OutputError

  case class ConflictError(code: String, message: String) extends OutputError

  case class ServerError(code: String, message: String) extends OutputError

  case class ServiceUnavailableError(code: String, message: String) extends OutputError

  case class UnknownError(code: String, message: String) extends OutputError

}
