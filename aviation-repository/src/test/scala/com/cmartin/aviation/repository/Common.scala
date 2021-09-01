package com.cmartin.aviation.repository

import scala.concurrent.Future

import JdbcRepositories.DataAccessObject

object Common {

  val configPath = "h2_dc"
  val dao = new TestDao(configPath)

  class TestDao(configPath: String) extends DataAccessObject(configPath) {
    import api._

    private val schema =
      countries.schema // ++
    // airports.schema

    def createSchema(): Future[Unit] = schema.create
    def dropSchema(): Future[Unit] = schema.dropIfExists
    def printSchema(): String = schema.createStatements.mkString("\n")
  }
}
