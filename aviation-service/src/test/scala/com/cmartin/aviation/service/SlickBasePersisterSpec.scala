package com.cmartin.aviation.service

import com.cmartin.aviation.repository.Common.schemaHelperProgram
import com.cmartin.aviation.repository.zioimpl.common.runtime
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterEach
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

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
