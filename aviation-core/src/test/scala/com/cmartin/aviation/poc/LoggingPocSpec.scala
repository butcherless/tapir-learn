package com.cmartin.aviation.poc

import com.cmartin.aviation.Commons._
import com.cmartin.aviation.domain.Model._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.ULayer
import zio.logging._
import zio.logging.slf4j.Slf4jLogger

class LoggingPocSpec extends AnyFlatSpec with Matchers {

  behavior of "LoggingPoc"

  "Service" should "create a Country" in {
    import LoggingPoc.Service.create
    val country = Country(CountryCode("es"), "Spain")
    val env: ULayer[Logging] = Slf4jLogger.make((_, message) => message)

    val program = create(country)

    val result = runtime.unsafeRun(
      program.provideLayer(env)
    )

    info(s"result: $result")

    result shouldBe Country(CountryCode("es"), "Spain")
  }

}
