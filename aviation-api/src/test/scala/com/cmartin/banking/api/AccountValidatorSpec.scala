package com.cmartin.banking.api

import com.cmartin.banking.api.AccountModel._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.NonEmptyChunk

class AccountValidatorSpec
    extends AnyFlatSpec with Matchers {

  import AccountValidatorSpec._

  behavior of "AccountValidator"

  "IBAN control" should "validate an account with valid data" in {
    // GIVEN
    val bankAccountView = BankAccountView(validIbanControl, validBank, validBranch, validControl, validNumber)
    // WHEN
    val result = AccountValidator.validate(bankAccountView).toEither

    result shouldBe Right(bankAccount)
  }

  it should "fail to validate an account with an invalid IBAN control" in {
    // GIVEN
    val bankAccountView = BankAccountView(invalidIbanControl, validBank, validBranch, validControl, validNumber)
    // WHEN
    val result = AccountValidator.validate(bankAccountView).toEither

    result shouldBe invalidIbanControlErrors
  }

  "Empty account" should "fail to validate an account with empty elements" in {
    // GIVEN
    val bankAccountView =
      BankAccountView(emptyIbanControl, emptyBank, emptyBranch, emptyNumberControl, emtpyAccountNumber)
    // WHEN
    val result = AccountValidator.validate(bankAccountView).toEither

    result shouldBe emptyAccountErrors
  }

  "Invalid Bank" should "fail to validate an account with an invalid bank code" in {
    // GIVEN
    val bankAccountView = BankAccountView(validIbanControl, invalidBank, validBranch, validControl, validNumber)
    // WHEN
    val result = AccountValidator.validate(bankAccountView).toEither

    result shouldBe invalidBankErrors
  }

  it should "fail to validate an account with an invalid bank code length" in {
    // GIVEN
    val bankAccountView = BankAccountView(validIbanControl, invalidBankLength, validBranch, validControl, validNumber)
    // WHEN
    val result = AccountValidator.validate(bankAccountView).toEither

    result shouldBe invalidBankLengthErrors
  }

  "Invalida data" should "fail to validate an account with invalid data" in {
    // GIVEN
    val bankAccountView =
      BankAccountView(invalidIbanControl, invalidBank, invalidBranch, invalidNumberControl, invalidNumber)
    // WHEN
    val result = AccountValidator.validate(bankAccountView).toEither

    result shouldBe invalidAccountErrors
  }

  "Invalid number" should "fail to validate an account with invalid number length and format" in {
    // GIVEN
    val bankAccountView =
      BankAccountView(validIbanControl, validBank, validBranch, validControl, invalidNumberLengthAndFormat)
    // WHEN
    val result = AccountValidator.validate(bankAccountView).toEither
    // THEN
    result shouldBe invalidNumberErrors
  }
}

object AccountValidatorSpec {
  val validIbanControl: IbanControl = IbanControl("ES83")
  val emptyIbanControl: IbanControl = IbanControl("")
  val invalidIbanControl: IbanControl = IbanControl("ES1")
  val validBank: BankCode = BankCode("2095")
  val invalidBank: BankCode = BankCode("20XY")
  val invalidBankLength: BankCode = BankCode("209")
  val emptyBank: BankCode = BankCode("")
  val validBranch: BranchCode = BranchCode("0517")
  val invalidBranch: BranchCode = BranchCode("X517")
  val emptyBranch: BranchCode = BranchCode("")

  val validControl: NumberControl = NumberControl("47")
  val emptyNumberControl: NumberControl = NumberControl("")
  val invalidNumberControl: NumberControl = NumberControl("4X")

  val validNumber: AccountNumber = AccountNumber("9400634176")
  val invalidNumber: AccountNumber = AccountNumber("940063417X")
  val emtpyAccountNumber: AccountNumber = AccountNumber("")
  val invalidNumberLengthAndFormat: AccountNumber = AccountNumber("1234567890X")

  val bankAccount: BankAccount =
    BankAccount(validIbanControl, validBank, validBranch, validControl, validNumber)

  val emptyIbanControlError: EmptyIbanControlError = EmptyIbanControlError(EMPTY_IBAN_CONTROL_MSG)
  val invalidIbanControlError: InvalidIbanControlError = InvalidIbanControlError(invalidIbanControl)

  val emptyBankError: EmptyBankError = EmptyBankError(EMPTY_BANK_CODE_MSG)
  val invalidBankError: InvalidBankError = InvalidBankError(invalidBank)
  val invalidBankLengthError: InvalidBankError = InvalidBankError(invalidBankLength)

  val emptyBranchError: EmptyBranchError = EmptyBranchError(EMPTY_BRANCH_CODE_MSG)
  val invalidBranchError: InvalidBranchError = InvalidBranchError(invalidBranch)

  val emptyControlError: EmptyControlError = EmptyControlError(EMPTY_CONTROL_DIGIT_MSG)
  val invalidNumberControlError: InvalidNumberControlFormat = InvalidNumberControlFormat(invalidNumberControl)

  val emptyNumberError: EmptyNumberError = EmptyNumberError(EMPTY_NUMBER_MSG)
  val invalidNumberLengthError: InvalidAccountNumberLength = InvalidAccountNumberLength(invalidNumber)
  val invalidNumberError: InvalidAccountNumberFormat = InvalidAccountNumberFormat(invalidNumber)
  val invalidNumberLength2Error: InvalidAccountNumberLength = InvalidAccountNumberLength(invalidNumberLengthAndFormat)
  val invalidNumber2Error: InvalidAccountNumberFormat = InvalidAccountNumberFormat(invalidNumberLengthAndFormat)

  val emptyIbanControlErrors = Left(
    NonEmptyChunk(emptyIbanControlError)
  )
  val invalidIbanControlErrors = Left(
    NonEmptyChunk(invalidIbanControlError)
  )
  val invalidBankErrors = Left(
    NonEmptyChunk(invalidBankError)
  )
  val invalidBankLengthErrors = Left(
    NonEmptyChunk(invalidBankLengthError)
  )
  val invalidNumberErrors = Left(
    NonEmptyChunk(
      invalidNumberLength2Error,
      invalidNumber2Error
    )
  )

  val emptyAccountErrors = Left(
    NonEmptyChunk(
      emptyIbanControlError,
      emptyBankError,
      emptyBranchError,
      emptyControlError,
      emptyNumberError
    )
  )

  val invalidAccountErrors = Left(
    NonEmptyChunk(
      invalidIbanControlError,
      invalidBankError,
      invalidBranchError,
      invalidNumberControlError,
      invalidNumberError
    )
  )

}
