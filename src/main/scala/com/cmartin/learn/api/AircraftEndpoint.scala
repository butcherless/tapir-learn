package com.cmartin.learn.api

import com.cmartin.learn.api.Model.AircraftDto
import org.json4s.JValue
import org.json4s.native.JsonMethods.parse

trait AircraftEndpoint {}

object AircraftEndpoint extends AircraftEndpoint {

  //TODO move to AircraftEndpoint companion object
  lazy val apiAircraftMIGExample: AircraftDto = AircraftDto("ec-mig", 3, "Boeing788", None)
  lazy val apiAircraftLVLExample: AircraftDto = AircraftDto("ec-lvl", 1, "Airbus332", None)
  lazy val apiAircraftNFZExample: AircraftDto = AircraftDto("EC-NFZ", 0, "AirbusA320N", Some(10002))

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
      |  "registration" : "EC-NFZ",
      |  "age" : 0,
      |  "model" : "AirbusA320N",
      |  "id": 10002
      |}
      |""".stripMargin

}
