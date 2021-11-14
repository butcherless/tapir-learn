package com.cmartin.banking.api

import com.cmartin.banking.api.AccountModel._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.NonEmptyChunk

class AccountValidatorSpec
    extends AnyFlatSpec with Matchers {

  import AccountValidatorSpec._

  behavior of "AccountValidator"

  "IBAN prefix" should "validate an account with a valid IBAN prefix" in {
    // GIVEN
    val bankAccountView = BankAccountView(validIbanPrefix, validBank, validBranch, validControl, validNumber)
    // WHEN
    val result = AccountValidator.validate(bankAccountView).toEither

    result shouldBe Right(bankAccount)
  }

  it should "fail to validate an account with an empty IBAN prefix" in {
    // GIVEN
    val bankAccountView = BankAccountView("", validBank, validBranch, validControl, validNumber)
    // WHEN
    val result = AccountValidator.validate(bankAccountView).toEither

    result shouldBe emptyIbanPrefixErrors
  }

  it should "fail to validate an account with an invalid IBAN prefix" in {
    // GIVEN
    val bankAccountView = BankAccountView(invalidPrefix, validBank, validBranch, validControl, validNumber)
    // WHEN
    val result = AccountValidator.validate(bankAccountView).toEither

    info(s"result: $result")
    result shouldBe invalidIbanPrefixErrors
  }

  it should "fail to validate an account with empty elements" in {
    // GIVEN
    val bankAccountView = BankAccountView("", "", "", "", "")
    // WHEN
    val result = AccountValidator.validate(bankAccountView).toEither

    result shouldBe emptyAccountErrors
  }

}

object AccountValidatorSpec {
  val validIbanPrefix = "ES83"
  val validBank = "2095"
  val validBranch = "0517"
  val validControl = "47"
  val validNumber = "9400634176"
  val invalidPrefix = "ES1"

  val bankAccount: BankAccount =
    BankAccount(validIbanPrefix, validBank, validBranch, validControl, validNumber)

  val emptyIbanPrefixError: EmptyIbanPrefixError = EmptyIbanPrefixError(EMPTY_IBAN_PREFIX_MSG)
  val invalidIbanPrefixError: InvalidIbanPrefixError = InvalidIbanPrefixError(invalidPrefix)
  val emptyBankError: EmptyBankError = EmptyBankError(EMPTY_BANK_CODE_MSG)
  val emptyBranchError: EmptyBranchError = EmptyBranchError(EMPTY_BRANCH_CODE_MSG)
  val emptyControlError: EmptyControlError = EmptyControlError(EMPTY_CONTROL_DIGIT_MSG)
  val emptyNumberError: EmptyNumberError = EmptyNumberError(EMPTY_NUMBER_MSG)

  val emptyIbanPrefixErrors = Left(
    NonEmptyChunk(emptyIbanPrefixError)
  )
  val invalidIbanPrefixErrors = Left(
    NonEmptyChunk(invalidIbanPrefixError)
  )

  val emptyAccountErrors = Left(
    NonEmptyChunk(
      emptyIbanPrefixError,
      emptyBankError,
      emptyBranchError,
      emptyControlError,
      emptyNumberError
    )
  )
}
