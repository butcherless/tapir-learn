package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.domain.Model._
import com.cmartin.aviation.repository.Model._
import slick.dbio.DBIO
import slick.interop.zio.DatabaseProvider
import slick.interop.zio.syntax._
import zio.Has
import zio.IO
import zio.RIO
import zio.Task
import zio.ZIO

import java.sql.SQLIntegrityConstraintViolationException

object common {
  val runtime = zio.Runtime.default

  implicit class Dbio2Zio[R](dbio: DBIO[R]) {
    def toZio: RIO[Has[DatabaseProvider], R] =
      ZIO.fromDBIO(dbio)
  }

  def manageNotFound[A](o: Option[A])(message: String): Task[A] = {
    o.fold[Task[A]](
      Task.fail(MissingEntityException(message))
    )(a => Task.succeed(a))
  }

  def manageError(e: Throwable): ServiceError = e match {
    case e: SQLIntegrityConstraintViolationException => DuplicateEntityError(e.getMessage())
    case e: MissingEntityException                   => MissingEntityError(e.getMessage())
    case e @ _                                       => UnexpectedServiceError(e.getMessage())
  }

}
