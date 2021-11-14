package com.cmartin.banking.api

object AccountModel {

  val EMPTY_IBAN_PREFIX_MSG = "IBAN prefix is empty"
  val EMPTY_BANK_CODE_MSG = "Bank code is empty"
  val EMPTY_BRANCH_CODE_MSG = "Branch code is empty"
  val EMPTY_CONTROL_DIGIT_MSG = "Control digit is empty"
  val EMPTY_NUMBER_MSG = "Account number is empty"

  sealed trait ValidationError {
    val message: String
  }
  case class EmptyIbanPrefixError(message: String = EMPTY_IBAN_PREFIX_MSG) extends ValidationError
  case class InvalidIbanPrefixError(message: String) extends ValidationError
  case class EmptyBankError(message: String = EMPTY_BANK_CODE_MSG) extends ValidationError
  case class EmptyBranchError(message: String = EMPTY_BRANCH_CODE_MSG) extends ValidationError
  case class EmptyControlError(message: String = EMPTY_CONTROL_DIGIT_MSG) extends ValidationError
  case class EmptyNumberError(message: String = EMPTY_NUMBER_MSG) extends ValidationError

  case class EmptyElementError(message: String) extends ValidationError

  // API model
  case class BankAccountView(
      prefix: String,
      bank: String,
      branch: String,
      control: String,
      number: String
  )

  // Domain model
  // TODO use prelude.Subtype
  case class BankAccount(
      prefix: String,
      bank: String,
      branch: String,
      control: String,
      number: String
  )
}
