package com.cmartin.aviation

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.json._

import WebApp.ApiModel._

class ZioJsonSpec
    extends AnyFlatSpec
    with Matchers {

  behavior of "ZioJson"

  it should "encode a country view" in {
    val country = CountryView(CountryCode("es"), "Spain")
    val result  = country.toJson
    info(s"country: $result")

    val code     = CountryCode("es")
    val codeJson = code.toJson
    info(s"code: $code")
  }
}
