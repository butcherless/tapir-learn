package com.cmartin.banking.api

import com.cmartin.banking.api.AccountModel._
import zio.prelude.Validation

import scala.util.matching.Regex

object AccountValidator {
  val IBAN_PREFIX_REGEX: Regex = """^[A-Z]{2}[0-9]{2}$""".r

  def validate(view: BankAccountView): Validation[ValidationError, BankAccount] = {
    Validation.validateWith(
      validateIbanPrefix(view.prefix),
      validateBankCode(view.bank),
      validateBranchCode(view.branch),
      validateControlCode(view.control),
      validateNumber(view.number)
    )(BankAccount)
  }

  private def validateIbanPrefix(prefix: String) = {
    for {
      nep <- validateEmptyText(prefix, EmptyIbanPrefixError())
      p <- validatePrefixFormat(nep)
    } yield p
  }

  private def validatePrefixFormat(prefix: String) = {
    Validation
      .fromPredicateWith(InvalidIbanPrefixError(prefix))(prefix)(IBAN_PREFIX_REGEX.matches)
  }

  private def validateBankCode(bank: String) = {
    for {
      b <- validateEmptyText(bank, EmptyBankError())
    } yield b // TODO safe conversion?
  }

  private def validateBranchCode(branch: String) = {
    for {
      b <- validateEmptyText(branch, EmptyBranchError())
    } yield b // TODO safe conversion?
  }

  private def validateControlCode(control: String) = {
    for {
      c <- validateEmptyText(control, EmptyControlError())
    } yield c // TODO safe conversion?
  }

  private def validateNumber(number: String) = {
    for {
      n <- validateEmptyText(number, EmptyNumberError())
    } yield n // TODO safe conversion?
  }

  // COMMON
  private def validateEmptyText(text: String, error: ValidationError): Validation[ValidationError, String] = {
    Validation
      .fromPredicateWith(error)(text)(_.nonEmpty)
  }

}
