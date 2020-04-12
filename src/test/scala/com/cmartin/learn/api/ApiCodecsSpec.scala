package com.cmartin.learn.api

import com.cmartin.learn.domain.DomainModel.{AirbusA332, Aircraft}
import io.circe.parser.{decode, parse}
import io.circe.syntax._
import io.circe.{Json, ParsingFailure}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ApiCodecsSpec
  extends AnyFlatSpec
    with Matchers
    with ApiCodecs {

  import ApiCodecsSpec._

  behavior of "Transfer codec"

  it should "T1 encode an Aircraft, object to json" in {
    val json: Json = aircraft.asJson
    info(json.toString)

    json shouldBe parse(aircraftJsonString).getOrElse("")
  }

  it should "T2 decode an Aircraft, json => object" in {
    val decodedAircraft = decode[Aircraft](aircraftJsonString)
    info(decodedAircraft.toString)

    decodedAircraft shouldBe Right(aircraft)
  }

  it should "T3 parse and decode an Aircraft, json => object" in {
    val parsedAircraft: Either[ParsingFailure, Json] = parse(aircraftJsonString)
    info(parsedAircraft.getOrElse("").toString)

    val decodedAircraft: Aircraft =
      parsedAircraft
        .fold(
          _ => throw new RuntimeException("parse error"),
          json => json.as[Aircraft]
        )
        .fold(
          _ => throw new RuntimeException("decode error"),
          identity
        )

    decodedAircraft shouldBe aircraft
  }

}

object ApiCodecsSpec {
  val aircraft = Aircraft("EC_LVL", 18, AirbusA332, 461)

  val aircraftJsonString =
    """
      |{
      |  "registration" : "EC_LVL",
      |  "age" : 18,
      |  "model" : "AirbusA332",
      |  "id" : 461
      |}
      |""".stripMargin
}
