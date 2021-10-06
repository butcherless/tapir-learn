package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.domain.Model._
import com.cmartin.aviation.repository.Model._
import slick.dbio.DBIO
import slick.interop.zio.DatabaseProvider
import slick.interop.zio.syntax._
import zio.Has
import zio.IO
import zio.Task
import zio.ZIO

object common {
  val runtime = zio.Runtime.default

  implicit class Dbio2Zio[R](ctx: (DBIO[R], DatabaseProvider)) {
    def toZio: IO[Throwable, R] =
      ZIO.fromDBIO(ctx._1)
        .provide(Has(ctx._2))
  }

  def manageNotFound[A](o: Option[A])(message: String): Task[A] = {
    o.fold[Task[A]](
      Task.fail(RepositoryException(message))
    )(a => Task.succeed(a))
  }

  def manageError(e: Throwable): ServiceError = e match {
    case e @ _ => UnexpectedServiceError(e.getMessage())
  }

}
