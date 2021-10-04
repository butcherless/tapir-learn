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

  implicit class CountryOptToDomain(dboOption: Option[CountryDbo]) {
    def toDomain: Option[Country] =
      dboOption.map(dbo => Country(CountryCode(dbo.code), dbo.name))
  }

}
