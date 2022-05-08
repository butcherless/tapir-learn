package com.cmartin.aviation.repository

import java.time.LocalDate

object Model {
  type LongTuple = (Long, Long)

  trait IdentifiedDbo[T] {
    val id: T
  }

  /** Identifier for a Long based Entity object
    */
  trait LongDbo extends IdentifiedDbo[Long]

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
      id: Long = 0L
  ) extends LongDbo

  /** A complex of runways and buildings for the take-off, landing, and
    * maintenance of civil aircraft, with facilities for passengers.
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
      id: Long = 0L
  ) extends LongDbo

  /** An organization providing a regular public service of air transport on one
    * or more routes.
    *
    * @param name
    *   the commercial name of the Airline, Iberia
    * @param iataCode
    *   iata 3-letter code
    * @param icaoCode
    *   icao 4-letter code
    * @param foundationDate
    *   the date on which its activity began, 1927-06-28
    * @param countryId
    *   Identifier of the airline to which it belongs
    * @param id
    *   entity identifier
    */
  final case class AirlineDbo(
      name: String,
      iataCode: String,
      icaoCode: String,
      foundationDate: LocalDate,
      countryId: Long = 0L,
      id: Long = 0L
  ) extends LongDbo

  /** A way or course taken in getting from a starting point to a destination.
    *
    * @param distance
    *   number of nautical miles between the two Airports, 957 nm.
    * @param originId
    *   Origin airport identifier
    * @param destinationId
    *   Destination airport identifier
    * @param id
    *   entity identifier
    */
  final case class RouteDbo(
      distance: Double,
      originId: Long,
      destinationId: Long,
      id: Long = 0L
  ) extends LongDbo

  final case class RepositoryException(message: String)
      extends RuntimeException(message)
  final case class MissingEntityException(message: String)
      extends RuntimeException(message)
}
