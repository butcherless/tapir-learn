package com.cmartin.learn.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.`Content-Type`
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.cmartin.learn.api.ActuatorApiSpec.contentTypeJson
import com.cmartin.learn.api.ApiModel.AircraftDto
import com.cmartin.learn.api.CommonEndpoint.BASE_API
import com.cmartin.learn.domain.DomainModel.Boeing788
import io.circe
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class AircraftApiSpec extends AnyFlatSpec with Matchers with ScalatestRouteTest {

  import AircraftApiSpec._

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
        info(either.toString)
        either.isRight shouldBe true
        either.map { aircraft =>
          aircraft shouldBe AircraftDto("ec-nei", 1, Boeing788.toString, None)
        }
      }
  }

}

object AircraftApiSpec {

  import io.circe.generic.auto._
  import io.circe.parser.decode

  def parseAircraft(json: String): Either[circe.Error, AircraftDto] =
    decode[AircraftDto](json)
}
