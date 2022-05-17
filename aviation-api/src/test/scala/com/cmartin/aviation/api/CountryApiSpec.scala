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
import zio.{IO, ZIO}

import scala.concurrent.duration._

import BaseEndpoint.baseApiPath
import CountryEndpoints.countriesResource
import CountryEndpoints.Implicits._
import Model._
import TestData._

class CountryApiSpec extends AnyFlatSpec with Matchers with MockFactory with ScalatestRouteTest {

  implicit val timeout = RouteTestTimeout(5.seconds.dilated)
  val countryService: CountryService = mock[CountryService]
  val countryApi: CountryApi = CountryApi(countryService)

  behavior of "CountryApi"

  "Post" should "create a Country" in {
    // G I V E N
    (countryService.create _)
      .expects(TestData.spainCountry)
      .returns(ZIO.succeed(TestData.spainCountry))
      .once()

    // W H E N
    Post(s"$baseApiPath/$countriesResource")
      .withEntity(TestData.spainCountryJson) ~>
      addHeader(jsonContentType) ~>
      countryApi.postRoute ~>
      // T H E N
      check {
        status shouldBe StatusCodes.Created
        decode[CountryView](entityAs[String]) shouldBe Right(CountryEndpoints.countryViewExample)
      }
  }

  it should "return BadRequest for an invalid Country, invalid code" in {
    // G I V E N

    // W H E N
    Post(s"$baseApiPath/$countriesResource")
      .withEntity(TestData.invalidCountryCodeJson) ~>
      addHeader(jsonContentType) ~>
      countryApi.postRoute ~>
      // T H E N
      check {
        status shouldBe StatusCodes.BadRequest
        decode[BadRequestError](entityAs[String]) shouldBe Right(
          BadRequestError(BadRequestError.toString, "[country code must have size: 2]")
        )
      }
  }

  "Get" should "retrieve a Country by code" in {
    // G I V E N
    val code = CountryCode("es")
    (countryService.findByCode _)
      .expects(code)
      .returns(ZIO.succeed(TestData.spainCountry))
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

  it should "return a BadRequest for an invalid country code" in {
    // G I V E N
    val code = CountryCode("esp")

    // W H E N
    Get(s"$baseApiPath/$countriesResource/${code}") ~>
      countryApi.getRoute ~>
      // T H E N
      check {
        status shouldBe StatusCodes.BadRequest
        decode[BadRequestError](entityAs[String]) shouldBe Right(
          BadRequestError(BadRequestError.toString, "[country code must have size: 2]")
        )
      }
  }

  it should "return a NotFound for a missing country code" in {
    // G I V E N
    val code = CountryCode("xy")
    (countryService.findByCode _)
      .expects(code)
      .returns(ZIO.fail(MissingEntityError(s"missing country for code: $code")))
      .once()

    // W H E N
    Get(s"$baseApiPath/$countriesResource/${code}") ~>
      countryApi.getRoute ~>
      // T H E N
      check {
        status shouldBe StatusCodes.NotFound
        info(entityAs[String])
        decode[NotFoundError](entityAs[String]) shouldBe Right(
          NotFoundError(NotFoundError.toString, s"missing country for code: $code")
        )
      }
  }

  "Update" should "update a Country" in {
    // G I V E N
    (countryService.update _)
      .expects(TestData.spainCountry)
      .returns(ZIO.succeed(TestData.spainCountry))
      .once()

    // W H E N
    Put(s"$baseApiPath/$countriesResource")
      .withEntity(TestData.spainCountryJson) ~>
      addHeader(jsonContentType) ~>
      countryApi.putRoute ~>
      // T H E N
      check {
        status shouldBe StatusCodes.OK
        decode[CountryView](entityAs[String]) shouldBe Right(CountryEndpoints.countryViewExample)
      }
  }

  "Delete" should "delete a Country" in {
    // G I V E N
    val code = CountryCode("es")

    (countryService.deleteByCode _)
      .expects(code)
      .returns(ZIO.succeed(1))
      .once()
    // W H E N
    Delete(s"$baseApiPath/$countriesResource/${code}") ~>
      countryApi.deleteRoute ~>
      check {
        // T H E N
        status shouldBe StatusCodes.NoContent
      }

  }
}
