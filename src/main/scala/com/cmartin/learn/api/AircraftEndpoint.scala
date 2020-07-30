package com.cmartin.learn.api

import com.cmartin.learn.api.ApiModel.AircraftDto
import org.json4s.JValue
import org.json4s.native.JsonMethods.parse

trait AircraftEndpoint {}

object AircraftEndpoint extends AircraftEndpoint {

  lazy val apiAircraftExample = AircraftDto("ec-mmg", 3, "AirbusA333", Some(1747L))

  lazy val jValueAircraftExample: JValue =
    parse(jsonStringAircraftExample)

  lazy val jsonStringAircraftExample: String =
    """
            |{
            |  "registration" : "EC-LVL",
            |  "age" : 18,
            |  "model" : "AirbusA332",
            |  "id" : 461
            |}
            |""".stripMargin

  lazy val j2ValueAircraftExample: JValue =
    parse(jsonStringAircraft2Example)

  lazy val jsonStringAircraft2Example: String =
    """
      |{
      |  "registration" : "ec-mmg", qgdq
      |  "age" : 3,
      |  "model" : "AirbusA333",
      |  "id": 1747
      |}
      |""".stripMargin

}
