package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Common.schemaHelperProgram
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterEach, EitherValues}
import zio.Runtime.{default => runtime}

abstract class SlickBaseRepositorySpec
    extends AnyFlatSpec
    with Matchers
    with EitherValues
    with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    runtime.unsafeRun(schemaHelperProgram)
  }
}
