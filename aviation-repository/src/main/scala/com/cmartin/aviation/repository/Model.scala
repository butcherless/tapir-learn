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

  final case class CountryDbo(
      name: String,
      code: String,
      id: Option[Long] = None
  ) extends LongDbo

  final case class AirportDbo(
      name: String,
      iataCode: String,
      icaoCode: String,
      countryId: Long = 0L,
      id: Option[Long] = None
  ) extends LongDbo

  final case class AirlineDbo(
      name: String,
      foundationDate: LocalDate,
      countryId: Long,
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
