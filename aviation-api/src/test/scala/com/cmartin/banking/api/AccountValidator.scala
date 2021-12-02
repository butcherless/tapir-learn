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

  /* Validation diagram
     bank   :---------|
     branch :---------|--|
     number :---------|  |
     numberControl       |--|
     ibanControl            |--> [ValidationError,BankAccount]
   */
  def validate(view: BankAccountView): Validation[ValidationError, BankAccount] = {
    for {
      bankBranchNumber <- Validation.validate(
        validateBankCode(view.bank),
        validateBranchCode(view.branch),
        validateNumber(view.number)
      )
      numberControl <- validateNumberControl(view.control, view.number)
      ibanControl <- validateIbanControl(view.ibanControl)
    } yield BankAccount(ibanControl, bankBranchNumber._1, bankBranchNumber._2, numberControl, bankBranchNumber._3)
  }

  private def validateNumberControl(
      control: NumberControl,
      number: AccountNumber
  ): Validation[ValidationError, NumberControl] = {
    for {
      nec <- validateEmptyText(control, EmptyControlError())
      fnc <- validateNumberControlFormat(NumberControl(nec))
      nc <- validateControlValue(fnc, number)
    } yield nc
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
  private def validateNumberControlFormat(code: NumberControl): Validation[ValidationError, NumberControl] = {
    Validation
      .fromPredicateWith(InvalidNumberControlFormat(code))(code)(NUMBER_CONTROL_REGEX.matches)
  }

  private def validateControlValue(
      control: NumberControl,
      number: AccountNumber
  ): Validation[ValidationError, NumberControl] = {
    for {
      // common to number control and iban control
      calculated <- Validation.succeed(calcControlDigit(number)) zipPar
        Validation.succeed(calcControlDigit(number.reverse)) map {
          case (inf, sup) => s"$sup$inf"
        }
      validated <- Validation
        .fromPredicateWith(InvalidNumberControl(calculated))(control)(_ == calculated)
    } yield validated
  }

  // dummy digit control
  private def calcControlDigit(number: String): Int = {
    number.map(_.toString.toInt).zipWithIndex.map { case (a, b) => a * b }.sum % 10
  }

  /*
    N U M B E R
   */
  private def validateNumber(number: AccountNumber): Validation[ValidationError, AccountNumber] = {
    for {
      nen <- validateEmptyText(number, EmptyNumberError())
      n <- validateNumberFormat(AccountNumber(nen))
    } yield n
  }
  private def validateNumberFormat(number: AccountNumber): Validation[ValidationError, AccountNumber] = {
    Validation
      .fromPredicateWith(InvalidAccountNumberLength(number))(number)(_.length == ACCOUNT_NUMBER_LENGTH)
      .zipParRight(
        Validation
          .fromPredicateWith(InvalidAccountNumberFormat(number))(number)(ACCOUNT_NUMBER_REGEX.matches)
      )
  }

  // COMMON
  private def validateEmptyText(text: String, error: ValidationError): Validation[ValidationError, String] = {
    Validation
      .fromPredicateWith(error)(text)(_.nonEmpty)
  }
}
