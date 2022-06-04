package com.cmartin.aviation.test

import com.cmartin.aviation.repository.JdbcRepositories.DataAccessObject
import com.cmartin.aviation.repository.Model._
import com.cmartin.aviation.test.Helper.SlickSchemaManager
import slick.basic.DatabaseConfig
import slick.jdbc.{JdbcBackend, JdbcProfile}
import zio.{Task, TaskLayer, ZIO, ZLayer}

import scala.concurrent.Future

object Common {

  val configPath = "h2_dc"
  val dao        = new TestDao(configPath)

  class TestDao(configPath: String)
      extends DataAccessObject(configPath) {
    import api._

    private val schema =
      countries.schema ++
        airports.schema ++
        routes.schema ++
        airlines.schema

    def createSchema(): Future[Unit] = schema.create
    def dropSchema(): Future[Unit]   = schema.dropIfExists
    def printSchema(): String        = schema.createStatements.mkString("\n")

    /* H E L P E R S */

    implicit val ec = scala.concurrent.ExecutionContext.global

    def insertAirport(countryDbo: CountryDbo)(airportDbo: AirportDbo): DBIO[Long] = {
      for {
        countryId <- dao.countryRepository.insert(countryDbo)
        id        <- dao.airportRepository.insert(updateCountryId(airportDbo)(countryId))
      } yield id
    }

    def updateCountryId(dbo: AirportDbo)(countryId: Long): AirportDbo =
      dbo.copy(countryId = countryId)

  }

  val dbLayer: TaskLayer[JdbcBackend#DatabaseDef] =
    ZLayer.scoped(
      ZIO.attempt(DatabaseConfig.forConfig[JdbcProfile]("h2_dc"))
        .map(_.db)
    )

  val schemaHelperProgram: Task[Unit] =
    (for {
      _ <- SlickSchemaManager.dropSchema()
      _ <- SlickSchemaManager.createSchema()
    } yield ()).provide(SlickSchemaManager.layer, dbLayer)

}
