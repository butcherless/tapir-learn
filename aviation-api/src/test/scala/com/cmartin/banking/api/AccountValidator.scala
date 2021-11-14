package com.cmartin.banking.api

import com.cmartin.banking.api.AccountModel._
import zio.prelude.Validation

import scala.util.matching.Regex

object AccountValidator {
  val IBAN_CONTROL_REGEX: Regex = """^[A-Z]{2}[0-9]{2}$""".r
  val BANK_CODE_REGEX: Regex = """^[0-9]{4}$""".r
  val BRANCH_CODE_REGEX: Regex = BANK_CODE_REGEX
  val NUMBER_CONTROL_REGEX: Regex = """^[0-9]{2}$""".r
  val ACCOUNT_NUMBER_REGEX: Regex = """^[0-9]+$""".r
  val ACCOUNT_NUMBER_LENGTH = 10

  def validate(view: BankAccountView): Validation[ValidationError, BankAccount] = {
    Validation.validateWith(
      validateIbanControl(view.ibanControl),
      validateBankCode(view.bank),
      validateBranchCode(view.branch),
      validateControlCode(view.control),
      validateNumber(view.number)
    )(BankAccount)
  }

  private def validateIbanControl(control: IbanControl): Validation[ValidationError, IbanControl] = {
    for {
      nep <- validateEmptyText(control, EmptyIbanControlError())
      p <- validateIbanControlFormat(IbanControl(nep))
    } yield p
  }

  private def validateIbanControlFormat(control: IbanControl): Validation[ValidationError, IbanControl] = {
    Validation
      .fromPredicateWith(InvalidIbanControlError(control))(control)(IBAN_CONTROL_REGEX.matches)
  }

  /*
    B A N K
   */
  private def validateBankCode(code: BankCode): Validation[ValidationError, BankCode] = {
    for {
      neb <- validateEmptyText(code, EmptyBankError())
      b <- validateBankFormat(BankCode(neb))
    } yield b
  }
  private def validateBankFormat(code: BankCode): Validation[ValidationError, BankCode] = {
    Validation
      .fromPredicateWith(InvalidBankError(code))(code)(BANK_CODE_REGEX.matches)
  }

  /*
    B R A N C H
   */
  private def validateBranchCode(branch: BranchCode): Validation[ValidationError, BranchCode] = {
    for {
      neb <- validateEmptyText(branch, EmptyBranchError())
      b <- validateBranchFormat(BranchCode(neb))
    } yield b
  }
  private def validateBranchFormat(code: BranchCode): Validation[ValidationError, BranchCode] = {
    Validation
      .fromPredicateWith(InvalidBranchError(code))(code)(BRANCH_CODE_REGEX.matches)
  }

  /*
    A C C O U N T   N U M B E R   C O N T R O L
   */
  private def validateControlCode(control: String): Validation[ValidationError, String] = {
    for {
      nec <- validateEmptyText(control, EmptyControlError())
      c <- validateNumberControlFormat(NumberControl(nec))
    } yield c
  }
  private def validateNumberControlFormat(code: NumberControl): Validation[ValidationError, NumberControl] = {
    Validation
      .fromPredicateWith(InvalidNumberControlFormat(code))(code)(NUMBER_CONTROL_REGEX.matches)
  }

  /*
    N U M B E R
   */
  private def validateNumber(number: String): Validation[ValidationError, String] = {
    for {
      nen <- validateEmptyText(number, EmptyNumberError())
      n <- validateNumberFormat(nen)
    } yield n
  }
  private def validateNumberFormat(number: String): Validation[ValidationError, String] = {
    Validation.validate(
      Validation
        .fromPredicateWith(InvalidAccountNumberLength(number))(number)(_.length == ACCOUNT_NUMBER_LENGTH),
      Validation
        .fromPredicateWith(InvalidAccountNumberFormat(number))(number)(ACCOUNT_NUMBER_REGEX.matches)
    ).map(_ => number)
  }

  // COMMON
  private def validateEmptyText(text: String, error: ValidationError): Validation[ValidationError, String] = {
    Validation
      .fromPredicateWith(error)(text)(_.nonEmpty)
  }
}
