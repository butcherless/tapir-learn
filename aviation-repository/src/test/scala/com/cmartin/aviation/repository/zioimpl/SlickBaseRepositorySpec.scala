package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Common.schemaHelperProgram
import com.cmartin.aviation.repository.zioimpl.common.runtime
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

abstract class SlickBaseRepositorySpec
    extends AnyFlatSpec
    with Matchers
    with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    runtime.unsafeRun(schemaHelperProgram)
  }
}
