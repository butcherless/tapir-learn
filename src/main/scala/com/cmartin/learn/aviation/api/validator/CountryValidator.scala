package com.cmartin.learn.aviation.api.validator
import zio.prelude.Validation
import com.cmartin.learn.aviation.api.Model._
import zio.IO

object CountryValidator {
  val COUNTRY_CODE_LENGTH = 2
  /* steps:
     - empty code
     - length == COUNTRY_CODE_LENGTH
     - convert toLower
   */
  def validateGetRequest(code: String): Validation[RestValidationError, String] =
    for {
      _ <- validateEmptyText(code, EmptyProperty("code property is empty"))
      _ <- validateCodeLength(code) //TODO refactor for generic use as validateEmptyText
    } yield code.toLowerCase()

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
