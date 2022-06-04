package com.cmartin.aviation.repository.zioimpl

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

    val dbLayer                                      = ZLayer.succeed(db)
    override def createSchema(): IO[Throwable, Unit] =
      SlickToZioSyntax.fromDBIO(
        (Tables.countries.schema ++
          Tables.airports.schema ++
          Tables.airlines.schema ++
          Tables.routes.schema).create
      ).provide(dbLayer)

    override def dropSchema(): IO[Throwable, Unit] =
      SlickToZioSyntax.fromDBIO(
        (Tables.countries.schema ++
          Tables.airports.schema ++
          Tables.airlines.schema ++
          Tables.routes.schema)
          .dropIfExists
      ).provide(dbLayer)

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
