package com.cmartin.aviation.repository

import com.cmartin.aviation.domain.Model._
import com.cmartin.aviation.repository.Model._

import scala.concurrent.duration._

object TestData {
  val waitTimeout: FiniteDuration = 5.seconds

  val spainCode = CountryCode("es")
  val spainText = "Spain"
  val spainCountry = Country(spainCode, spainText)
  val spainDbo = CountryDbo(spainText, spainCode)
  val updatedSpainText = "SPAIN"

  val portugalCode = CountryCode("pt")
  val portugalText = "Portugal"
  val portugalCountry = Country(portugalCode, portugalText)
  val portugalDbo = CountryDbo(portugalText, portugalCode)

  val countrySequence = Seq(spainDbo, portugalDbo)

  // AIRPORT
  val madName = "Barajas"
  val madIataCode = "MAD"
  val madIcaoCode = "LEMD"
  val madDbo = AirportDbo(madName, madIataCode, madIcaoCode)
  val bcnIataCode = "BCN"
  val bcnDbo = AirportDbo("El Prat", bcnIataCode, "LEBL")
  val tfnDbo = AirportDbo("Los Rodeos", "TFN", "GCXO")
  val updatedMadText = "MADRID BARAJAS"

  val airportSequence = Seq(madDbo, bcnDbo)

  val lisDbo = AirportDbo("Portela", "LIS", "LPPT")
}
