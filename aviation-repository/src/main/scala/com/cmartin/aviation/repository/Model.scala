package com.cmartin.aviation.repository

import java.time.LocalDate

object Model {
  type LongTuple = (Long, Long)

  trait IdentifiedDbo[T] {
    val id: T
  }

  /** Identifier for a Long based Entity object
    */
  trait LongDbo extends IdentifiedDbo[Option[Long]]

  /** Identifier for a N <-> M relationship object
    */
  trait RelationDbo extends IdentifiedDbo[LongTuple]

  /** A nation with its own government, occupying a particular territory.
    *
    * @param name
    *   country nmme
    * @param code
    *   country iso-2 code
    * @param id
    *   entity identifier
    */
  final case class CountryDbo(
      name: String,
      code: String,
      id: Option[Long] = None
  ) extends LongDbo

  /** A complex of runways and buildings for the take-off, landing, and maintenance of civil aircraft, with facilities
    * for passengers.
    *
    * @param name
    *   airport name
    * @param iataCode
    *   iata 3-letter code
    * @param icaoCode
    *   icao 4-letter code
    * @param countryId
    *   Identifier of the country to which it belongs
    * @param id
    *   entity identifier
    */
  final case class AirportDbo(
      name: String,
      iataCode: String,
      icaoCode: String,
      countryId: Long = 0L,
      id: Option[Long] = None
  ) extends LongDbo

  final case class AirlineDbo(
      name: String,
      iataCode: String,
      icaoCode: String,
      foundationDate: LocalDate,
      countryId: Long = 0L,
      id: Option[Long] = None
  ) extends LongDbo

  final case class RouteDbo(
      distance: Double,
      originId: Long,
      destinationId: Long,
      id: Option[Long] = None
  ) extends LongDbo

  object TableNames {
    val airlines = "AIRLINES"
    val airports = "AIRPORTS"
    val countries = "COUNTRIES"
    val fleet = "FLEET"
    val flights = "FLIGHTS"
    val journeys = "JOURNEYS"
    val routes = "ROUTES"
    val aircraftJourney = "AIRCRAFT_JOURNEY"
  }
}
