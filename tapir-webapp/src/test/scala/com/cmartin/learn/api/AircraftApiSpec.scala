package com.cmartin.learn.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.`Content-Type`
import akka.http.scaladsl.testkit.RouteTestTimeout
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.testkit.TestDuration
import com.cmartin.learn.api.ActuatorApiSpec.contentTypeJson
import com.cmartin.learn.api.CommonEndpoint.BASE_API
import com.cmartin.learn.api.Model.AircraftDto
import com.cmartin.learn.api.Model.AircraftType
import io.circe
import org.json4s.DefaultFormats
import org.json4s.JValue
import org.json4s._
import org.json4s.ext.EnumNameSerializer
import org.json4s.native.JsonMethods
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration._

class AircraftApiSpec extends AnyFlatSpec with Matchers with ScalatestRouteTest {

  import AircraftApiSpec._

  implicit val timeout: RouteTestTimeout = RouteTestTimeout(5.seconds.dilated)
  implicit val serialization: Serialization = org.json4s.native.Serialization
  implicit val formats: Formats = DefaultFormats + new EnumNameSerializer(AircraftType)

  behavior of "AircraftApi API"

  it should "retrieve an Aircraft via /aircrafts endpoint" in {
    // W H E N
    Get(s"$BASE_API/aircrafts") ~>
      AircraftApi.routes ~>
      // T H E N
      check {
        status shouldBe StatusCodes.OK
        header[`Content-Type`] shouldBe Some(contentTypeJson)
        val either = parseAircraft(entityAs[String])
        either.isRight shouldBe true
        either.map { aircraft =>
          aircraft shouldBe AircraftEndpoint.apiAircraftMIGExample
        }
      }
  }

  it should "retrieve an Aircraft via /jvalues endpoint" in {
    // W H E N
    Get(s"$BASE_API/jvalues") ~>
      Json4sApi.routes ~>
      // T H E N
      check {
        status shouldBe StatusCodes.OK
        header[`Content-Type`] shouldBe Some(contentTypeJson)
        val response = JsonMethods.parse(entityAs[String])
        response shouldBe AircraftEndpoint.jValueAircraftExample
      }
  }

  it should "retrieve an Aircraft via /json4s endpoint" in {
    // W H E N
    Get(s"$BASE_API/json4s") ~>
      Json4sApi.routes ~>
      // T H E N
      check {
        status shouldBe StatusCodes.OK
        header[`Content-Type`] shouldBe Some(contentTypeJson)
        val response: JValue = JsonMethods.parse(entityAs[String])
        response
          .extract[AircraftDto] shouldBe AircraftEndpoint.apiAircraftMIGExample
      }
  }

  it should "create an Aircraft via /jvalues endpoint" in {

    Post(s"$BASE_API/jvalues")
      .withEntity(AircraftEndpoint.jsonStringAircraftExample) ~>
      Json4sApi.postJsonRoute ~>
      // THEN
      check {
        status shouldBe StatusCodes.Created
        val response: JValue = JsonMethods.parse(entityAs[String])
        response shouldBe AircraftEndpoint.jValueAircraftExample
      }
  }

  it should "create an Aircraft via /j2values endpoint" in {
    Post(s"$BASE_API/j2values")
      .withEntity(AircraftEndpoint.jsonStringAircraft2Example) ~>
      Json4sApi.postEntityRoute ~>
      // THEN
      check {
        status shouldBe StatusCodes.Created
        val response: JValue = JsonMethods.parse(entityAs[String])
        response shouldBe AircraftEndpoint.j2ValueAircraftExample
      }
  }

}

object AircraftApiSpec {

  import io.circe.generic.auto._
  import io.circe.parser.decode

  def parseAircraft(json: String): Either[circe.Error, AircraftDto] =
    decode[AircraftDto](json)
}
