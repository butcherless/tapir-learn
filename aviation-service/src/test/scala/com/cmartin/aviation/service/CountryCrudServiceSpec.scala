package com.cmartin.aviation.service

import com.cmartin.aviation.domain.Model._
import com.cmartin.aviation.port.{CountryCrudRepository, CountryService}
import com.cmartin.aviation.repository.zioimpl.CountryPersisterLive
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.{Has, URLayer}
import zio.logging.Logging
class CountryCrudServiceSpec extends AnyFlatSpec with Matchers {

  behavior of "CountryCrudService"

  /*
  val env: TaskLayer[Has[CountryRepository] with Has[AirportRepository]] =
    (testEnv >>> SlickCountryRepository.live) ++
      (testEnv >>> SlickAirportRepository.live)
   */

//TODO
  ignore should "be implemented..." in {
    val code = CountryCode("es")
    val country = Country(code, "Spain")

    val env: URLayer[Has[Logging] with Has[CountryCrudRepository], Has[CountryCrudServiceLive]] =
      CountryCrudServiceLive.layer

    val repoLayer = CountryPersisterLive.layer

    val l1 = Logging

    val l2 = CountryPersisterLive.layer
    //>>> CountryCrudServiceLive.layer

    // W H E N
    val program = CountryService.create(country)

    /*    val result = runtime.unsafeRun(
      program.provideLayer(env)
    )
     */
    //info(s"result: $result")

    //assert(true, "todo")
  }

  it should "build a service layer" in {
    val code = CountryCode("es")
    info(s"code: $code")
    val unwrap = CountryCode.unwrap(code)
    info(s"unwrapped: $unwrap")
    val toString = code.toString()
    toString shouldBe unwrap
  }
}
