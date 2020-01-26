package com.cmartin.learn.domain

import com.cmartin.learn.api.ApiModel.ApiAircraft
import com.cmartin.learn.domain.DomainModel.{AirbusA320, Aircraft, AircraftModel, Boeing788}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DomainModelSpec
  extends AnyFlatSpec
    with Matchers
    with ApiConverters {

  behavior of "Aircraft model"

  it should "toString object with the same symbols" in {
    val aType: AircraftModel = AirbusA320

    info(aType.toString)
    aType.toString shouldBe "AirbusA320"
  }

  it should "convert api to model Aircrafts" in {
    val registration = "EC-NBX"
    val age = 2
    val model = "Boeing788"
    val apiAircraft = ApiAircraft(registration, age, model, Some(1L))

    val aircraft = apiToModel(apiAircraft)

    aircraft shouldBe Aircraft(registration, age, Boeing788, 1L)
  }

  it should "fail when trying to decode invalid aircraft model" in {
    val registration = "EC-NBX"
    val age = 2
    val model = "InvalidModel"
    val apiAircraft = ApiAircraft(registration, age, model)

    a[CustomMappingError] should be thrownBy apiToModel(apiAircraft)
  }

  it should "convert model to api Aircrafts" in {
    val registration = "EC-NBX"
    val age = 2
    val model = Boeing788
    val aircraft = Aircraft(registration, age, model, 1L)

    val apiAircraft = modelToApi(aircraft)

    apiAircraft shouldBe ApiAircraft(registration, age, "Boeing788", Some(1L))
  }

}
