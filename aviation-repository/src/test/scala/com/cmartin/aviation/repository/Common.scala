package com.cmartin.aviation.repository

import com.cmartin.aviation.repository.JdbcRepositories.DataAccessObject
import com.cmartin.aviation.repository.Model._
import com.cmartin.aviation.repository.zioimpl.SlickSchemaHelper
import com.typesafe.config.ConfigFactory
import slick.interop.zio.DatabaseProvider
import slick.jdbc.JdbcProfile
import zio.Has
import zio.ZLayer

import scala.concurrent.Future
import scala.jdk.CollectionConverters._

object Common {

  val configPath = "h2_dc"
  val dao = new TestDao(configPath)

  class TestDao(configPath: String) extends DataAccessObject(configPath) {
    import api._

    private val schema =
      countries.schema ++
        airports.schema ++
        routes.schema ++
        airlines.schema

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

  val config = ConfigFactory.parseMap(
    Map(
      "url" -> "jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1",
      "driver" -> "org.h2.Driver",
      "connectionPool" -> "disabled"
    ).asJava
  )

  val testEnv: ZLayer[Any, Throwable, Has[DatabaseProvider]] =
    (ZLayer.succeed(config) ++
      ZLayer.succeed[JdbcProfile](slick.jdbc.H2Profile)) >>> DatabaseProvider.live

  val schemaHelperLayer =
    (ZLayer.succeed(config) ++
      ZLayer.succeed[JdbcProfile](slick.jdbc.H2Profile)) >>> DatabaseProvider.live >>> SlickSchemaHelper.live

  val schemaHelperProgram = (for {
    _ <- SlickSchemaHelper.dropSchema()
    _ <- SlickSchemaHelper.createSchema()
  } yield ()).provideLayer(schemaHelperLayer)

}
