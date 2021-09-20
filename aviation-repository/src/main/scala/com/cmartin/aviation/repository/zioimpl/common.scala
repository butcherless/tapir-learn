package com.cmartin.aviation.repository.zioimpl

import slick.dbio.DBIO
import slick.interop.zio.DatabaseProvider
import slick.interop.zio.syntax._
import zio.Has
import zio.IO
import zio.ZIO

object common {
  val runtime = zio.Runtime.default

  implicit class Dbio2Zio[R](ctx: (DBIO[R], DatabaseProvider)) {
    def toZio: IO[Throwable, R] =
      ZIO.fromDBIO(ctx._1).provide(Has(ctx._2))
  }

}
