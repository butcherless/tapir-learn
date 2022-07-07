package com.cmartin.aviation.test

import com.cmartin.aviation.domain.Model._
import com.cmartin.aviation.repository.Model._

import java.time.LocalDate
import scala.concurrent.duration._

object TestData {
  val waitTimeout: FiniteDuration = 5.seconds

  val spainCode        = CountryCode("es")
  val spainText        = "Spain"
  val spainCountry     = Country(spainCode, spainText)
  val spainDbo         = CountryDbo(spainText, spainCode)
  val updatedSpainText = "SPAIN"

  val portugalCode    = CountryCode("pt")
  val portugalText    = "Portugal"
  val portugalCountry = Country(portugalCode, portugalText)
  val portugalDbo     = CountryDbo(portugalText, portugalCode)

  val countrySequence = Seq(spainDbo, portugalDbo)

  // AIRPORT
  val madName        = "Barajas"
  val madIataCode    = IataCode("MAD")
  val madIcaoCode    = IcaoCode("LEMD")
  val madAirport     = Airport(madName, madIataCode, madIcaoCode, spainCountry)
  val madDbo         = AirportDbo(madName, madIataCode, madIcaoCode)
  val bcnIataCode    = "BCN"
  val bcnDbo         = AirportDbo("El Prat", bcnIataCode, "LEBL")
  val tfnDbo         = AirportDbo("Los Rodeos", "TFN", "GCXO")
  val updatedMadText = "MADRID BARAJAS"

  val airportSequence = Seq(madDbo, bcnDbo)

  val lisDbo = AirportDbo("Portela", "LIS", "LPPT")

  // AIRLINE
  val ibeName           = "Iberia"
  val updatedIbeText    = "IBERIA"
  val ibeIataCode       = IataCode("ib")
  val ibeIcaoCode       = IcaoCode("ibe")
  val ibeFoundationDate = LocalDate.of(1927, 6, 28)
  val ibeAirline        = Airline(ibeName, ibeIataCode, ibeIcaoCode, ibeFoundationDate, spainCountry)
  val ibeDbo            = AirlineDbo(ibeName, ibeIataCode, ibeIcaoCode, ibeFoundationDate)

  val aeaName           = "Air Europa"
  val aeaIataCode       = IataCode("ux")
  val aeaIcaoCode       = IcaoCode("aea")
  val aeaFoundationDate = LocalDate.of(1986, 11, 21)
  val aeaAirline        = Airline(aeaName, aeaIataCode, aeaIcaoCode, aeaFoundationDate, spainCountry)
  val aeaDbo            = AirlineDbo(aeaName, aeaIataCode, aeaIcaoCode, aeaFoundationDate)

  val tapName           = "TAP Air Portugal"
  val tapIataCode       = "tp"
  val tapIcaoCode       = "tap"
  val tapFoundationDate = LocalDate.of(1945, 3, 14)
  val tapDbo            = AirlineDbo(tapName, tapIataCode, tapIcaoCode, tapFoundationDate)

  // ROUTE
  val madBcnDistance: Double = 262.0
  val madTfnDistance: Double = 957.0
  val bcnTfnDistance: Double = 1185.0
}
