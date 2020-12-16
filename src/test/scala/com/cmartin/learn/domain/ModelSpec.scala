package com.cmartin.learn.domain

import com.cmartin.learn.api.Model.AircraftDto
import com.cmartin.learn.domain.Model.AirbusA320
import com.cmartin.learn.domain.Model.Aircraft
import com.cmartin.learn.domain.Model.AircraftModel
import com.cmartin.learn.domain.Model.Boeing788
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ModelSpec extends AnyFlatSpec with Matchers with ApiConverters {

  behavior of "Aircraft model"

  it should "toString object with the same symbols" in {
    val aType: AircraftModel = AirbusA320

    info(aType.toString)
    aType.toString shouldBe "AirbusA320"
  }

  it should "convert api to model Aircraft" in {
    val registration = "EC-NBX"
    val age          = 2
    val model        = "Boeing788"
    val apiAircraft  = AircraftDto(registration, age, model, Some(1L))

    val aircraft = apiToModel(apiAircraft)

    aircraft shouldBe Aircraft(registration, age, Boeing788, 1L)
  }

  it should "fail when trying to decode invalid aircraft model" in {
    val registration = "EC-NBX"
    val age          = 2
    val model        = "InvalidModel"
    val apiAircraft  = AircraftDto(registration, age, model)

    a[CustomMappingError] should be thrownBy apiToModel(apiAircraft)
  }

  it should "convert model to api Aircrafts" in {
    val registration = "EC-NBX"
    val age          = 2
    val model        = Boeing788
    val aircraft     = Aircraft(registration, age, model, 1L)

    val apiAircraft = modelToApi(aircraft)

    apiAircraft shouldBe AircraftDto(registration, age, "Boeing788", Some(1L))
  }

  it should "convert a string to a Result" in {
    stringToResult("Success") shouldBe Model.Success
    stringToResult("Warning") shouldBe Model.Warning
    stringToResult("Error") shouldBe Model.Error
  }

  it should "fail when trying to convert an invalid Result" in {
    a[CustomMappingError] should be thrownBy stringToResult("invalid-result")
  }

  it should "fail when trying to convert an empty Result" in {
    a[CustomMappingError] should be thrownBy stringToResult("")
  }

  it should "convert a string to an AircraftModel" in {
    stringToAircraftModel("AirbusA320") shouldBe Model.AirbusA320
    stringToAircraftModel("AirbusA332") shouldBe Model.AirbusA332
    stringToAircraftModel("Boeing788") shouldBe Model.Boeing788
    stringToAircraftModel("Boeing737NG") shouldBe Model.Boeing737NG
  }

}
