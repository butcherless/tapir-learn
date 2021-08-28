package com.cmartin.aviation.api.validator

import com.cmartin.aviation.api.Model._
import com.cmartin.aviation.domain.Model._
import zio.IO
import zio.prelude.Validation

object CountryValidator {
  val COUNTRY_CODE_LENGTH = 2
  /* steps:
     - empty code
     - length == COUNTRY_CODE_LENGTH
     - convert toLower
   */
  def validateGetRequest(code: String): Validation[RestValidationError, CountryCode] =
    for {
      _ <- validateEmptyText(code, EmptyProperty("code property is empty"))
      _ <- validateCodeLength(code) //TODO refactor for generic use as validateEmptyText
    } yield CountryCode(code.toLowerCase())

  // TODO move to common validator
  private def validateEmptyText(text: String, error: RestValidationError): Validation[RestValidationError, String] = {
    Validation
      .fromPredicateWith(error)(text)(_.nonEmpty)
  }

  private def validateCodeLength(code: String): Validation[RestValidationError, String] = {
    Validation
      .fromPredicateWith(InvalidCodeLength(s"country code has size: $COUNTRY_CODE_LENGTH"))(code)(
        _.size == COUNTRY_CODE_LENGTH
      )
  }

  /*
     C O N V E R T E R S
   */
  implicit class ValidationToIO[A](validated: Validation[DomainError, A]) {
    def toIO: IO[ValidationError, A] =
      validated.fold(
        nec => IO.fail(ValidationError(s"${nec.map(_.message).mkString("[", ",", "]")}")),
        a => IO.succeed(a)
      )
  }

}
