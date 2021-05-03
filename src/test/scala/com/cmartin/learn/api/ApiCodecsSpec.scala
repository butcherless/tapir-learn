package com.cmartin.learn.api

import com.cmartin.learn.api.Model.AircraftDto
import com.cmartin.learn.api.Model.AircraftType
import io.circe.Json
import io.circe.ParsingFailure
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.parser.parse
import io.circe.syntax._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ApiCodecsSpec extends AnyFlatSpec with Matchers with ApiCodecs {

  import ApiCodecsSpec._

  behavior of "Transfer codec"

  it should "T1 encode an Aircraft, object to json" in {
    val json: Json = aircraft.asJson
    info(json.toString)

    json shouldBe parse(aircraftJsonString).getOrElse("")
  }

  it should "T2 decode an Aircraft, json => object" in {
    val decodedAircraft = decode[AircraftDto](aircraftJsonString)
    info(decodedAircraft.toString)

    decodedAircraft shouldBe Right(aircraft)
  }

  it should "T3 parse and decode an Aircraft, json => object" in {
    val parsedAircraft: Either[ParsingFailure, Json] = parse(aircraftJsonString)
    info(parsedAircraft.getOrElse("").toString)

    val decodedAircraft: AircraftDto =
      parsedAircraft
        .fold(
          _ => throw new RuntimeException("parse error"),
          json => json.as[AircraftDto]
        )
        .fold(
          _ => throw new RuntimeException("decode error"),
          identity
        )

    decodedAircraft shouldBe aircraft
  }

}

object ApiCodecsSpec {
  val aircraft = AircraftDto("EC_LVL", 18, AircraftType.Airbus332, Some(461))

  val aircraftJsonString =
    """
      |{
      |  "registration" : "EC_LVL",
      |  "age" : 18,
      |  "model" : "Airbus332",
      |  "id" : 461
      |}
      |""".stripMargin
}
