package com.cmartin.learn.aviation.domain

import zio.prelude.Subtype

object Model {

  //TODO move to domain
  trait DomainError {
    val message: String
  }

  sealed trait ServiceError extends DomainError

  object CountryCode extends Subtype[String]
  type CountryCode = CountryCode.Type

  case class Country(code: CountryCode, name: String)
}
