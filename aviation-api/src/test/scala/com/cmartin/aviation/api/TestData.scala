package com.cmartin.aviation.api

import com.cmartin.aviation.domain.Model._

object TestData {
  val spainCountryCode = CountryCode("es")
  val spainText = "Spain"
  val spainCountry = Country(spainCountryCode, spainText)
}
