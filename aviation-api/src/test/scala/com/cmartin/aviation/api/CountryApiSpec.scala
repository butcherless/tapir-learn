package com.cmartin.aviation.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.cmartin.aviation.domain.CountryService
import com.cmartin.aviation.domain.Model._
import io.circe.generic.auto._
import io.circe.parser.decode
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.IO

import BaseEndpoint.baseApiPath
import CountryEndpoints.countriesResource
import CountryEndpoints.Implicits._
import Model.CountryView

class CountryApiSpec extends AnyFlatSpec with Matchers with MockFactory with ScalatestRouteTest {

  val countryService: CountryService = mock[CountryService]
  val countryApi: CountryApi = CountryApi(countryService)

  behavior of "CountryApiSpec"

  "Get" should "retrieve a Country by code" in {
    // G I V E N
    val code = CountryCode("es")
    (countryService.findByCode _)
      .expects(code)
      .returns(IO.succeed(TestData.spainCountry))
      .once()

    // W H E N
    Get(s"$baseApiPath/$countriesResource/${code}") ~>
      countryApi.getRoute ~>
      // T H E N
      check {
        status shouldBe StatusCodes.OK
        decode[CountryView](entityAs[String]) shouldBe Right(CountryEndpoints.countryViewExample)
      }
  }

  ignore should "returns a BadRequest for an invalid country code" in {
    // G I V E N
    val code = CountryCode("esp")

    // W H E N
    Get(s"$baseApiPath/$countriesResource/${code}") ~>
      countryApi.getRoute ~>
      // T H E N
      check {
        status shouldBe StatusCodes.BadRequest
        //decode[CountryView](entityAs[String]) shouldBe Right(CountryEndpoints.countryViewExample)
      }

  }
}
