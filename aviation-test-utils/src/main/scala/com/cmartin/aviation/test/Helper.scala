package com.cmartin.aviation.test

import com.cmartin.aviation.repository.zioimpl.Tables
import com.cmartin.aviation.repository.zioimpl.common.SlickToZioSyntax
import slick.jdbc.{JdbcBackend, JdbcProfile}
import zio.{IO, RLayer, ZIO, ZLayer}

object Helper {

  trait SchemaManager {
    def createSchema(): IO[Throwable, Unit]

    def dropSchema(): IO[Throwable, Unit]
  }

  case class SlickSchemaManager(db: JdbcBackend#DatabaseDef)
      extends SchemaManager
      with JdbcProfile {
    import api._

    override def createSchema(): IO[Throwable, Unit] =
      SlickToZioSyntax.fromDBIO(
        (Tables.countries.schema ++
          Tables.airports.schema ++
          Tables.airlines.schema ++
          Tables.routes.schema).create
      ).provideService(db)

    override def dropSchema(): IO[Throwable, Unit] =
      SlickToZioSyntax.fromDBIO(
        (Tables.countries.schema ++
          Tables.airports.schema ++
          Tables.airlines.schema ++
          Tables.routes.schema)
          .dropIfExists
      ).provideService(db)

  }

  object SlickSchemaManager {
    val layer: RLayer[JdbcBackend#DatabaseDef, SlickSchemaManager] =
      ZLayer.fromFunction(db => SlickSchemaManager(db))

    def createSchema(): ZIO[SchemaManager, Throwable, Unit] =
      ZIO.serviceWithZIO[SchemaManager](_.createSchema())

    def dropSchema(): ZIO[SchemaManager, Throwable, Unit] =
      ZIO.serviceWithZIO[SchemaManager](_.dropSchema())
  }
}
