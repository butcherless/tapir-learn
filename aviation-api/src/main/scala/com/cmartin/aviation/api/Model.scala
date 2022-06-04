package com.cmartin.aviation.api

import com.cmartin.aviation.domain.Model._

import java.time.{LocalDate, LocalDateTime}

object Model {

  sealed trait ApiError        extends ProgramError
  sealed trait ValidationError extends ApiError

  abstract class DescribedError extends ValidationError {
    val message: String
  }

  case class InvalidCountryCode(message: String) extends DescribedError

  case class EmptyProperty(message: String)     extends DescribedError
  case class InvalidCodeLength(message: String) extends DescribedError

  case class InvalidNameCharacters(message: String) extends DescribedError

  case class ValidationErrors(message: String, exception: Option[Throwable] = None) extends ValidationError

  // TODO opaque Type, regex, constraints
  case class CountryView(
      code: CountryCode,
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

  case class BuildInfoView(
      name: String,
      version: String,
      scalaVersion: String,
      sbtVersion: String,
      gitCommit: String,
      builtAtMillis: LocalDateTime
  )

}
