package com.cmartin.learn.apizio

import com.cmartin.learn.apizio.ActuatorApi.getSwaggerVersion
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import ActuatorApi.Artifact

class PocApiZioSpec extends AnyFlatSpec with Matchers {

  import PocApiZioSpec._

  behavior of "PocApiZio"

  it should "read pom version from filesystem" in {
    val program = getSwaggerVersion()

    val result = runtime.unsafeRun(program)

    result shouldBe Artifact("org.webjars", "swagger-ui", "3.51.1")
  }

}
object PocApiZioSpec {

  val runtime = zio.Runtime.default

}
