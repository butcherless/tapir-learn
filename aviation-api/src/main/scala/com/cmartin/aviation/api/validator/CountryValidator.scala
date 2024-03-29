package com.cmartin.aviation.api.validator

import com.cmartin.aviation.api.Model._
import com.cmartin.aviation.domain.Model._
import zio.{IO, ZIO}
import zio.prelude.Validation

object CountryValidator {
  val COUNTRY_CODE_LENGTH                                                  = 2
  /* steps:
     - empty code
     - length == COUNTRY_CODE_LENGTH
     - convert toLower
   */
  def validateCode(code: String): Validation[ValidationError, CountryCode] =
    for {
      _ <- validateEmptyText(code, EmptyProperty("code property is empty"))
      _ <- validateCodeLength(code) // TODO refactor for generic use as validateEmptyText
    } yield CountryCode(code.toLowerCase())

  // TODO move to common validator
  private def validateEmptyText(text: String, error: ValidationError): Validation[ValidationError, String] = {
    Validation
      .fromPredicateWith(error)(text)(_.nonEmpty)
  }

  private def validateCodeLength(code: String): Validation[ValidationError, String] = {
    Validation
      .fromPredicateWith(InvalidCodeLength(s"country code must have size: $COUNTRY_CODE_LENGTH"))(code)(
        _.size == COUNTRY_CODE_LENGTH
      )
  }

  def validatePostRequest(request: CountryView): Validation[ValidationError, Country] = {
    Validation.validateWith(
      validateCode(request.code),
      validateName(request.name)
    )(Country)
  }

  def validatePutRequest(request: CountryView): Validation[ValidationError, Country] = {
    validatePostRequest(request)
  }

  // TODO add regex validation, copy from existing
  def validateName(name: String): Validation[ValidationError, String] = {
    Validation.succeed(name)
  }

  def validateDeleteRequest(code: String): Validation[ValidationError, CountryCode] = {
    validateCode(code)
  }

  /*
     C O N V E R T E R S
   */
  implicit class ValidationToIO[A](validated: Validation[ValidationError, A]) {
    def toIO: IO[ValidationError, A] =
      validated.fold(
        nec => ZIO.fail(ValidationErrors(s"${nec.map(_.message).mkString("[", ",", "]")}")),
        a => ZIO.succeed(a)
      )
  }

}
