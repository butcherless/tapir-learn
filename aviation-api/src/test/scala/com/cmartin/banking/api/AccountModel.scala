package com.cmartin.banking.api

import zio.prelude.Subtype

object AccountModel {

  val EMPTY_IBAN_CONTROL_MSG = "IBAN control is empty"
  val EMPTY_BANK_CODE_MSG = "Bank code is empty"
  val EMPTY_BRANCH_CODE_MSG = "Branch code is empty"
  val EMPTY_CONTROL_DIGIT_MSG = "Control digit is empty"
  val EMPTY_NUMBER_MSG = "Account number is empty"

  sealed trait ValidationError {
    val message: String
  }
  case class EmptyIbanControlError(message: String = EMPTY_IBAN_CONTROL_MSG) extends ValidationError
  case class InvalidIbanControlError(message: String) extends ValidationError

  case class EmptyBankError(message: String = EMPTY_BANK_CODE_MSG) extends ValidationError
  case class InvalidBankError(message: String) extends ValidationError

  case class EmptyBranchError(message: String = EMPTY_BRANCH_CODE_MSG) extends ValidationError
  case class InvalidBranchError(message: String = EMPTY_BANK_CODE_MSG) extends ValidationError

  case class EmptyControlError(message: String = EMPTY_CONTROL_DIGIT_MSG) extends ValidationError
  case class EmptyNumberError(message: String = EMPTY_NUMBER_MSG) extends ValidationError
  case class InvalidNumberControlFormat(message: String) extends ValidationError
  case class InvalidNumberControl(message: String) extends ValidationError

  case class InvalidAccountNumberLength(message: String) extends ValidationError
  case class InvalidAccountNumberFormat(message: String) extends ValidationError

  case class EmptyElementError(message: String) extends ValidationError

  object IbanControl extends Subtype[String]
  type IbanControl = IbanControl.Type
  object BankCode extends Subtype[String]
  type BankCode = BankCode.Type
  object BranchCode extends Subtype[String]
  type BranchCode = BranchCode.Type
  object NumberControl extends Subtype[String]
  type NumberControl = NumberControl.Type
  object AccountNumber extends Subtype[String]
  type AccountNumber = AccountNumber.Type

  // API model
  case class BankAccountView(
      ibanControl: IbanControl,
      bank: BankCode,
      branch: BranchCode,
      control: NumberControl,
      number: AccountNumber
  )

  // Domain model
  // TODO use prelude.Subtype
  case class BankAccount(
      ibanControl: String,
      bank: String,
      branch: String,
      control: String,
      number: String
  )
}
