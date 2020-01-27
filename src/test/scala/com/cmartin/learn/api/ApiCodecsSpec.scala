package com.cmartin.learn.api

import io.circe.Json
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ApiCodecsSpec
  extends AnyFlatSpec
    with Matchers
    with ApiCodecs {

  behavior of "Transfer codec"

  it should "encode EUR" in {
    currencyEncoder(ApiModel.EUR) shouldBe Json.fromString(ApiModel.EUR.toString)
  }

  it should "encode USD" in {
    currencyEncoder(ApiModel.USD) shouldBe Json.fromString(ApiModel.USD.toString)
  }

}
