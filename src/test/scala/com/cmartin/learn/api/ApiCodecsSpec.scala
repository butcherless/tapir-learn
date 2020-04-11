package com.cmartin.learn.api

import com.cmartin.learn.domain.DomainModel.{AirbusA332, Aircraft}
import io.circe.Json
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ApiCodecsSpec
  extends AnyFlatSpec
    with Matchers
    with ApiCodecs {

  import ApiCodecsSpec._

  behavior of "Transfer codec"

  it should "T1 encode EUR" in {
    currencyEncoder(ApiModel.EUR) shouldBe Json.fromString(ApiModel.EUR.toString)
  }

  it should "T2 encode USD" in {
    currencyEncoder(ApiModel.USD) shouldBe Json.fromString(ApiModel.USD.toString)
  }

  it should "T3 encode an Aircraft" in {
    val json: Json = ApiCodecs.aircraftEncoder(aircraft)

    json shouldBe io.circe.parser.parse(aircraftJsonString).getOrElse("")
  }

  it should "T4 decode an Aircraft" in {
    val aircraftDecoded = io.circe.parser.decode[Aircraft](aircraftJsonString)

    info(aircraftDecoded.toString)
  }

}

object ApiCodecsSpec {
  val aircraft = Aircraft("EC_LVL", 17, AirbusA332, 456)

  val aircraftJsonString =
    """
      |{
      |  "registration" : "EC_LVL",
      |  "age" : 17,
      |  "model" : "AirbusA332",
      |  "id" : 456
      |}
      |""".stripMargin
}
