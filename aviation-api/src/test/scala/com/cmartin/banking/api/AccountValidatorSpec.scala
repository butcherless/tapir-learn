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
    val bankAccountView = BankAccountView(emptyIbanControl, emptyBank, emptyBranch, "", "")
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
    val bankAccountView =
      BankAccountView(invalidIbanControl, invalidBank, invalidBranch, invalidNumberControl, invalidNumber)
    // WHEN
    val result = AccountValidator.validate(bankAccountView).toEither

    result shouldBe invalidAccountErrors
  }
}

object AccountValidatorSpec {
  val validIbanControl: IbanControl = IbanControl("ES83")
  val emptyIbanControl: IbanControl = IbanControl("")
  val invalidIbanControl = IbanControl("ES1")
  val validBank = BankCode("2095")
  val invalidBank = BankCode("20XY")
  val invalidBankLength = BankCode("209")
  val emptyBank = BankCode("")
  val validBranch = BranchCode("0517")
  val invalidBranch = BranchCode("X517")
  val emptyBranch = BranchCode("")
  val validControl = NumberControl("47")
  val invalidNumberControl = NumberControl("4X")
  //TODO AccountNumber type
  val validNumber = "9400634176"
  val invalidNumber = "940063417X"

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
  val invalidNumberError: InvalidAccountNumberFormat = InvalidAccountNumberFormat(invalidNumber)

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
