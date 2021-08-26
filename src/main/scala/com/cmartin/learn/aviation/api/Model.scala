package com.cmartin.learn.aviation.api

import java.time.LocalDate

object Model {
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

  case class ServiceUnavailableError(code: String, message: String)
      extends OutputError

  case class UnknownError(code: String, message: String) extends OutputError

}
