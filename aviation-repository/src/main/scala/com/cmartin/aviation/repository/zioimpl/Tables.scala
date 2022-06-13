package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Model._
import slick.jdbc.JdbcProfile
import slick.lifted.{Index, ProvenShape}

import java.time.LocalDate

object Tables extends JdbcProfile {
  import CommonAbstractions.Table.LongBasedTable
  import api._

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

  /* C O U N T R Y
   */
  class Countries(tag: Tag)
      extends LongBasedTable[CountryDbo](tag, TableNames.countries) {
    // property columns:
    def name: Rep[String] = column[String]("NAME")
    def code: Rep[String] = column[String]("CODE")

    // mapper function
    def * : ProvenShape[CountryDbo] =
      (name, code, id).<>(CountryDbo.tupled, CountryDbo.unapply)

    // indexes
    def nameIndex: Index =
      index("name_idx", name, unique = true)
    def codeIndex: Index =
      index("code_idx", code, unique = true)
  }

  val countries = TableQuery[Countries]

  /* A I R P O R T
   */
  final class Airports(tag: Tag)
      extends LongBasedTable[AirportDbo](tag, TableNames.airports) {
    // property columns:
    def name: Rep[String] = column[String]("NAME")
    def iataCode: Rep[String] = column[String]("IATA_CODE")
    def icaoCode: Rep[String] = column[String]("ICAO_CODE")

    // foreign columns:
    def countryId: Rep[Long] = column[Long]("COUNTRY_ID")

    def * : ProvenShape[AirportDbo] =
      (name, iataCode, icaoCode, countryId, id).<>(AirportDbo.tupled, AirportDbo.unapply)

    // foreign keys
    def country = foreignKey("FK_COUNTRY_AIRPORT", countryId, countries)(_.id)

    // indexes
    def iataIndex = index("airport_iataCode_index", iataCode, unique = true)
    def icaoIndex = index("airport_icaoCode_index", icaoCode, unique = true)
  }

  val airports = TableQuery[Airports]

  /* A I R L I N E
   */
  final class Airlines(tag: Tag)
      extends LongBasedTable[AirlineDbo](tag, TableNames.airlines) {
    // property columns:
    def name = column[String]("NAME")
    def iataCode: Rep[String] = column[String]("IATA_CODE")
    def icaoCode: Rep[String] = column[String]("ICAO_CODE")
    def foundationDate = column[LocalDate]("FOUNDATION_DATE")

    // foreign columns:
    def countryId = column[Long]("COUNTRY_ID")

    def * = (name, iataCode, icaoCode, foundationDate, countryId, id).<>(AirlineDbo.tupled, AirlineDbo.unapply)

    // foreign keys
    def country = foreignKey("FK_COUNTRY_AIRLINE", countryId, countries)(_.id)

    // indexes
    def nameIndex = index("airline_name_index", name, unique = false)
    def iataIndex = index("airline_iataCode_index", iataCode, unique = true)
    def icaoIndex = index("airline_icaoCode_index", icaoCode, unique = true)
  }

  val airlines = TableQuery[Airlines]

  /* R O U T E
   */
  final class Routes(tag: Tag)
      extends LongBasedTable[RouteDbo](tag, TableNames.routes) {
    // property columns:
    def distance = column[Double]("DISTANCE")

    // foreign key columns:
    def originId = column[Long]("ORIGIN_ID")

    def destinationId = column[Long]("DESTINATION_ID")

    def * = (distance, originId, destinationId, id).<>(RouteDbo.tupled, RouteDbo.unapply)

    // foreign keys
    def origin =
      foreignKey("FK_ORIGIN_AIRPORT", originId, airports)(
        origin => origin.id,
        onDelete = ForeignKeyAction.Cascade
      )

    def destination =
      foreignKey("FK_DESTINATION_AIRPORT", destinationId, airports)(
        destination => destination.id,
        onDelete = ForeignKeyAction.Cascade
      )

    // indexes, compound
    def originDestinationIndex =
      index("origin_destination_index", (originId, destinationId), unique = true)
  }

  val routes = TableQuery[Routes]
}
