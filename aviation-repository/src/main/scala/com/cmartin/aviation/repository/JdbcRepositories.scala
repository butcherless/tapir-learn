package com.cmartin.aviation.repository
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.lifted.ForeignKeyQuery
import slick.lifted.Index
import slick.lifted.PrimaryKey
import slick.lifted.ProvenShape

import scala.concurrent.Future

import JdbcDefinitions.BaseDefinitions

object JdbcRepositories {
  import JdbcDefinitions._
  import AbstractRepositories._

  trait AviationRepositories extends BaseDefinitions {
    self: JdbcProfile =>
    import Model._
    import api._

    lazy val countries = TableQuery[CountryTable]
    lazy val airports = TableQuery[AirportTable]
    lazy val routes = TableQuery[RouteTable]

    /* C O U N T R Y
     */
    final class CountryTable(tag: Tag) extends LongBasedTable[CountryDbo](tag, TableNames.countries) {
      // property columns:
      def name: Rep[String] = column[String]("NAME")
      def code: Rep[String] = column[String]("CODE")

      // mapper function
      def * : ProvenShape[CountryDbo] =
        (name, code, id.?).<>(CountryDbo.tupled, CountryDbo.unapply)

      // indexes
      def codeIndex: Index =
        index("code_idx", code, unique = true)
    }

    /* A I R P O R T
     */
    final class AirportTable(tag: Tag) extends LongBasedTable[AirportDbo](tag, TableNames.airports) {
      // property columns:
      def name: Rep[String] = column[String]("NAME")

      def iataCode: Rep[String] = column[String]("IATA_CODE")

      def icaoCode: Rep[String] = column[String]("ICAO_CODE")

      // foreign columns:
      def countryId: Rep[Long] = column[Long]("COUNTRY_ID")

      def * : ProvenShape[AirportDbo] =
        (name, iataCode, icaoCode, countryId, id.?).<>(AirportDbo.tupled, AirportDbo.unapply)

      // foreign keys
      def country = foreignKey("FK_COUNTRY_AIRPORT", countryId, countries)(_.id)

      // indexes
      def iataIndex = index("iataCode_index", iataCode, unique = true)
    }

    /* A I R P O R T
     */
    final class RouteTable(tag: Tag) extends LongBasedTable[RouteDbo](tag, TableNames.routes) {
      // property columns:
      def distance = column[Double]("DISTANCE")

      // foreign key columns:
      def originId = column[Long]("ORIGIN_ID")

      def destinationId = column[Long]("DESTINATION_ID")

      def * = (distance, originId, destinationId, id.?).<>(RouteDbo.tupled, RouteDbo.unapply)

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

    /*  R E P O S I T O R I E S
     */
    final class CountrySlickRepository
        extends AbstractLongRepository[CountryDbo, CountryTable]
        with AbstractCountryRepository[DBIO] {

      override val entities: TableQuery[CountryTable] = countries

      override def findByCode(code: String): DBIO[Option[CountryDbo]] = {
        entities.filter(_.code === code).result.headOption
      }
    }

    final class AirportSlickRepository
        extends AbstractLongRepository[AirportDbo, AirportTable]
        with AbstractAirportRepository[DBIO] {

      override val entities: TableQuery[AirportTable] = airports

      override def findByIataCode(code: String): DBIO[Option[AirportDbo]] = {
        entities.filter(_.iataCode === code).result.headOption
      }

      override def findByIcaoCode(code: String): DBIO[Option[AirportDbo]] = {
        entities.filter(_.icaoCode === code).result.headOption
      }

      override def findByCountryCode(code: String): DBIO[Seq[AirportDbo]] = {
        val query = for {
          airport <- entities
          country <- airport.country if country.code === code
        } yield airport

        query.result
      }
    }

    final class RouteSlickRepository
        extends AbstractLongRepository[RouteDbo, RouteTable]
        with AbstractRouteRepository[DBIO] {

      override val entities: TableQuery[RouteTable] = routes
    }
  }

  class DataAccessObject(configPath: String) extends JdbcProfile with AviationRepositories {
    val config = DatabaseConfig.forConfig[JdbcProfile](configPath)

    implicit def runAction[A](action: api.DBIO[A]): Future[A] =
      config.db.run(action)

    val countryRepository = new CountrySlickRepository
    val airportRepository = new AirportSlickRepository
    val routeRepository = new RouteSlickRepository
  }
}
