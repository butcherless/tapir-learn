package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.domain.Model._
import com.cmartin.aviation.repository.Model._
import slick.jdbc.{JdbcActionComponent, JdbcBackend, JdbcProfile}
import zio.{RIO, Task, ZIO, ZLayer}

import java.sql.SQLIntegrityConstraintViolationException

object common {

  // Slick <-> ZIO integration and syntax
  object SlickToZioSyntax
      extends JdbcProfile
      with JdbcActionComponent.OneRowPerStatementOnly {
    import api._

    def fromDBIO[R](dbio: => DBIO[R]): RIO[JdbcBackend#Database, R] = for {
      db <- ZIO.service[JdbcBackend#Database]
      r  <- ZIO.fromFuture(_ => db.run(dbio))
    } yield r
  }

  object Implicits
      extends JdbcProfile
      with JdbcActionComponent.OneRowPerStatementOnly {
    import api._
    implicit class Dbio2Zio[R](dbio: DBIO[R]) {
      def toZio: RIO[JdbcBackend#Database, R] =
        SlickToZioSyntax.fromDBIO(dbio)
    }

    implicit class QueryToLayer[T](zio: RIO[JdbcBackend#Database, T]) {
      def provideDbLayer(db: JdbcBackend#Database): Task[T] =
        zio.provide(ZLayer.succeed(db))
    }

  }

  def manageNotFound[A](o: Option[A])(message: String): Task[A] = {
    o.fold[Task[A]](
      ZIO.fail(MissingEntityException(message))
    )(a => ZIO.succeed(a))
  }

  def manageError(e: Throwable): ServiceError = e match {
    case e: SQLIntegrityConstraintViolationException => DuplicateEntityError(e.getMessage)
    case e: MissingEntityException                   => MissingEntityError(e.getMessage)
    case e @ _                                       => UnexpectedServiceError(e.getMessage)
  }

}
