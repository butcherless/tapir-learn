package com.cmartin.aviation.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.RouteTestTimeout
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.testkit.TestDuration
import com.cmartin.aviation.domain.Model._
import com.cmartin.aviation.port.CountryService
import io.circe.generic.auto._
import io.circe.parser.decode
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.IO

import scala.concurrent.duration._

import BaseEndpoint.baseApiPath
import CountryEndpoints.countriesResource
import CountryEndpoints.Implicits._
import Model._

class CountryApiSpec extends AnyFlatSpec with Matchers with MockFactory with ScalatestRouteTest {

  implicit val timeout = RouteTestTimeout(5.seconds.dilated)
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

  it should "WIP returns a BadRequest for an invalid country code" in {
    // G I V E N
    val code = CountryCode("esp")

    // W H E N
    Get(s"$baseApiPath/$countriesResource/${code}") ~>
      countryApi.getRoute ~>
      // T H E N
      check {
        status shouldBe StatusCodes.BadRequest
        info(entityAs[String])
        decode[BadRequestError](entityAs[String]) shouldBe Right(
          BadRequestError(BadRequestError.toString, "[country code must have size: 2]")
        )
      }

  }
}
