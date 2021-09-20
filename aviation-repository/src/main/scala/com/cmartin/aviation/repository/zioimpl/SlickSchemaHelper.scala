package com.cmartin.aviation.repository.zioimpl

import slick.interop.zio.DatabaseProvider
import slick.interop.zio.syntax._
import zio.Has
import zio.IO
import zio.ZIO
import zio.ZLayer

object SlickSchemaHelper {

  trait SchemaHelper {
    def createSchema(): IO[Throwable, Unit]
    def dropSchema(): IO[Throwable, Unit]
  }

  val live: ZLayer[Has[DatabaseProvider], Throwable, Has[SchemaHelper]] =
    ZLayer.fromServiceM { db =>
      db.profile.map { profile =>
        import profile.api._

        new SchemaHelper {
          override def createSchema(): IO[Throwable, Unit] =
            ZIO.fromDBIO(
              (Tables.countries.schema ++
                Tables.airports.schema)
                .create
            )
              .provide(Has(db))

          override def dropSchema(): IO[Throwable, Unit] =
            ZIO.fromDBIO(
              (Tables.countries.schema ++
                Tables.airports.schema)
                .dropIfExists
            )
              .provide(Has(db))

        }
      }
    }

  def createSchema(): ZIO[Has[SchemaHelper], Throwable, Unit] =
    ZIO.serviceWith[SchemaHelper](_.createSchema())

  def dropSchema(): ZIO[Has[SchemaHelper], Throwable, Unit] =
    ZIO.serviceWith[SchemaHelper](_.dropSchema())
}
