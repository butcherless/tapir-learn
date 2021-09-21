package com.cmartin.aviation.domain

import zio.prelude.Subtype

object Model {

  //TODO move to domain
  trait ProgramError {
    val message: String
  }

  sealed trait ServiceError extends ProgramError
  trait RepositoryError extends ProgramError

  case class MissingEntityError(message: String) extends ServiceError
  case class UnexpectedServiceError(message: String) extends ServiceError

  object CountryCode extends Subtype[String]
  type CountryCode = CountryCode.Type

  object IataCode extends Subtype[String]
  type IataCode = IataCode.Type

  case class Country(
      code: CountryCode,
      name: String
  )

  case class Airport(
      name: String,
      iataCode: IataCode,
      icaoCode: String,
      country: Country
  )
}
