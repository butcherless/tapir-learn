package com.cmartin.aviation.application

import com.cmartin.aviation.domain.CountryCrudService
import com.cmartin.aviation.domain.Model._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.cmartin.aviation.Commons._

class CountryCrudServiceSpec extends AnyFlatSpec with Matchers {

  behavior of "CountryCrudService"

//TODO
  it should "be implemented..." in {
    val service = CountryCrudService()
    val code = CountryCode("es")
    val country = Country(code, "Spain")

    // W H E N
    val program = service.create(country)

    val result = runtime.unsafeRun(
      program.provideLayer(loggingEnv)
    )
    info(s"result: $result")

    assert(true, "todo")
  }
}
