package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Model.LongDbo
import com.cmartin.aviation.repository.SlickInfrastructure.CountryTable.LongBasedTable
import slick.dbio.DBIO
import slick.interop.zio.DatabaseProvider
import slick.interop.zio.syntax._
import slick.jdbc.JdbcProfile
import slick.lifted.TableQuery
import zio.{Has, IO, ZIO}

object common {
  val runtime = zio.Runtime.default

  trait Abstractions { self: JdbcProfile =>
    abstract class AbstractRepository[E <: LongDbo, T <: LongBasedTable[E]] {
      val entities: TableQuery[T]
    }
  }

  implicit class Dbio2Zio[R](ctx: (DBIO[R], DatabaseProvider)) {
    def toZio: IO[Throwable, R] =
      ZIO.fromDBIO(ctx._1).provide(Has(ctx._2))
  }
}
