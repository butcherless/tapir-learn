package com.cmartin.learn.domain

import com.cmartin.learn.api.Model.AircraftDto
import com.cmartin.learn.api.Model.AircraftType
import com.cmartin.learn.domain.Model.AircraftModel._
import com.cmartin.learn.domain.Model._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ModelSpec extends AnyFlatSpec with Matchers with ApiConverters {

  behavior of "Aircraft model"

  it should "toString object with the same symbols" in {
    val aType = AirbusA320

    info(aType.toString)
    aType.toString shouldBe "AirbusA320"
  }

  it should "convert api to model Aircraft" in {
    val registration = "EC-NBX"
    val age = 2
    val model = AircraftType.Boeing788
    val apiAircraft = AircraftDto(registration, age, model, Some(1L))

    val aircraft = apiToModel(apiAircraft)

    aircraft shouldBe Aircraft(registration, age, AircraftModel.Boeing788, 1L)
  }

  /* TODO
  it should "fail when trying to decode invalid aircraft model" in {
    val registration = "EC-NBX"
    val age          = 2
    val model        = "InvalidModel"
    val apiAircraft  = AircraftDto(registration, age, model)

    a[CustomMappingError] should be thrownBy apiToModel(apiAircraft)
  }
   */

  it should "convert model to api Aircrafts" in {
    val registration = "EC-NBX"
    val age = 2
    val model = Boeing788
    val aircraft = Aircraft(registration, age, model, 1L)

    val apiAircraft = modelToApi(aircraft)

    //TODO apiAircraft shouldBe AircraftDto(registration, age, model, Some(1L))
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
    AircraftType.AirbusA320.toModel shouldBe AircraftModel.AirbusA320
    AircraftType.AirbusA320N.toModel shouldBe AircraftModel.AirbusA320N
    AircraftType.Airbus332.toModel shouldBe AircraftModel.AirbusA332
    AircraftType.AirbusA333.toModel shouldBe AircraftModel.AirbusA333
    AircraftType.Boeing737NG.toModel shouldBe AircraftModel.Boeing737NG
    AircraftType.Boeing788.toModel shouldBe AircraftModel.Boeing788
    AircraftType.Boeing789.toModel shouldBe AircraftModel.Boeing789
  }

}
