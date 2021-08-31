package com.cmartin.aviation.api

import com.cmartin.aviation.domain.Model._
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.headers.`Content-Type`

object TestData {
  val spainCountryCode = CountryCode("es")
  val spainText = "Spain"
  val spainCountry = Country(spainCountryCode, spainText)

  val jsonContentType = `Content-Type`(`application/json`)

  // J S O N   D A T A
  val spainCountryJson: String =
    """
    |{
    | "code": "es",
    | "name": "Spain"
    |}
    """.stripMargin

  val invalidCountryCodeJson: String =
    """
    |{
    | "code": "xyz",
    | "name": "Spain"
    |}
    """.stripMargin
}
