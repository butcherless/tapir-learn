package com.cmartin.aviation.domain

import zio.prelude.Subtype

object Model {

  //TODO move to domain
  trait ProgramError {
    val message: String
  }

  sealed trait ServiceError extends ProgramError

  case class MissingEntityError(message: String) extends ServiceError

  object CountryCode extends Subtype[String]
  type CountryCode = CountryCode.Type

  case class Country(code: CountryCode, name: String)
}
