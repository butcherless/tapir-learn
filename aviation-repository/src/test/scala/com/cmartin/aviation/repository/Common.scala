package com.cmartin.aviation.repository

import scala.concurrent.Future

import Model._
import JdbcRepositories.DataAccessObject

object Common {

  val configPath = "h2_dc"
  val dao = new TestDao(configPath)

  class TestDao(configPath: String) extends DataAccessObject(configPath) {
    import api._

    private val schema =
      countries.schema ++
        airports.schema ++
        routes.schema

    def createSchema(): Future[Unit] = schema.create
    def dropSchema(): Future[Unit] = schema.dropIfExists
    def printSchema(): String = schema.createStatements.mkString("\n")

    /* H E L P E R S */

    implicit val ec = scala.concurrent.ExecutionContext.global

    def insertAirport(countryDbo: CountryDbo)(airportDbo: AirportDbo): DBIO[Long] = {
      for {
        countryId <- dao.countryRepository.insert(countryDbo)
        id <- dao.airportRepository.insert(updateCountryId(airportDbo)(countryId))
      } yield id
    }

    def updateCountryId(dbo: AirportDbo)(countryId: Long): AirportDbo =
      dbo.copy(countryId = countryId)

  }
}
