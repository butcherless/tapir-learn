package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.domain.Model._
import com.cmartin.aviation.repository.Model._

object Mappers {

  implicit class CountryToDbo(country: Country) {
    def toDbo: CountryDbo =
      CountryDbo(country.name, country.code)
  }

  implicit class AirportToDbo(airport: Airport) {
    def toDbo(countryId: Long): AirportDbo =
      AirportDbo(airport.name, airport.iataCode, airport.icaoCode, countryId)
  }
  implicit class AirlineToDbo(airline: Airline) {
    def toDbo(countryId: Long): AirlineDbo =
      AirlineDbo(airline.name, airline.iataCode, airline.icaoCode, airline.foundationDate, countryId)
  }

  implicit class CountryOptToDomain(dboOption: Option[CountryDbo]) {
    def toDomain: Option[Country] =
      dboOption.map(dbo =>
        Country(CountryCode(dbo.code), dbo.name)
      )
  }

  /*
  implicit class AirportOptToDomain(dboOption: Option[AirportDbo]) {
    def toDomain(countryDbo: CountryDbo): Option[Airport] =
      dboOption.map(dbo =>
        Airport(
          dbo.name,
          IataCode(dbo.iataCode),
          IcaoCode(dbo.icaoCode),
          Country(CountryCode(countryDbo.code), countryDbo.name)
        )
      )
  }*/

  implicit class AirportToDomain(dbo: AirportDbo) {
    def toDomain(countryDbo: CountryDbo): Airport =
      Airport(
        dbo.name,
        IataCode(dbo.iataCode),
        IcaoCode(dbo.icaoCode),
        Country(CountryCode(countryDbo.code), countryDbo.name)
      )
  }
  implicit class AirlineToDomain(dbo: AirlineDbo) {
    def toDomain(countryDbo: CountryDbo): Airline =
      Airline(
        dbo.name,
        IataCode(dbo.iataCode),
        IcaoCode(dbo.icaoCode),
        dbo.foundationDate,
        Country(CountryCode(countryDbo.code), countryDbo.name)
      )
  }

}
