package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Common._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterEach, EitherValues}

abstract class SlickBaseRepositorySpec
    extends AnyFlatSpec
    with Matchers
    with EitherValues
    with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    unsafeRun(schemaHelperProgram)
  }
}
