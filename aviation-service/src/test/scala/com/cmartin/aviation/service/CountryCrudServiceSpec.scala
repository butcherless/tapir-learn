package com.cmartin.aviation.service

import com.cmartin.aviation.Commons._
import com.cmartin.aviation.domain.Model._
import com.cmartin.aviation.port.CountryRepository
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CountryCrudServiceSpec extends AnyFlatSpec with Matchers {

  behavior of "CountryCrudService"

//TODO
  ignore should "be implemented..." in {
    val countryRepository: CountryRepository = ???
    val service = CountryCrudService(countryRepository)
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
