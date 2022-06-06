package com.cmartin.aviation.service

import com.cmartin.aviation.test.Common.schemaHelperProgram
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterEach, EitherValues}
import zio.Runtime.{default => runtime}

//TODO replace repositories with inMemory (STM-TMap) implementations
abstract class SlickBasePersisterSpec
    extends AnyFlatSpec
    with Matchers
    with MockFactory
    with EitherValues
    with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    runtime.unsafeRun(schemaHelperProgram)
  }
}
