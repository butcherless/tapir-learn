package com.cmartin.aviation.api

import com.cmartin.aviation.Commons._
import com.cmartin.aviation.domain.Model._
import zio.{CancelableFuture, IO, ZIO, ZLayer}
import zio.logging._
import zio.logging.slf4j.Slf4jLogger
import BaseEndpoint._
import Model._

import scala.concurrent.Future

object Common {

  type ApiResponse[A] = ZIO[Logging, ProgramError, A]
  type Api2Response[A] = IO[ProgramError, A]

  val loggingEnv: ZLayer[Any, Nothing, Logging] =
    Slf4jLogger.make((_, message) => message)

  def run[A](program: ApiResponse[A]): RouteResponse[A] = {
    runtime.unsafeRunToFuture(
      program
        .provideLayer(loggingEnv)
        .mapError(handleError)
        .either
    )
  }

  def run2[A](program: Api2Response[A]): RouteResponse[A] = {
    runtime.unsafeRunToFuture(
      program
        .mapError(handleError)
        .either
    )
  }

  // map ServiceError to ApiError, default case
  def handleError(error: ProgramError): OutputError =
    error match {
      case ValidationErrors(message, _) => BadRequestError(BadRequestError.toString, message)
      case e: ServiceError              => handleServiceError(e)
      case e @ _                        => UnknownError(UnknownError.toString, e.message)
    }

  def handleServiceError(error: ServiceError): OutputError =
    error match {
      case MissingEntityError(message) => NotFoundError(NotFoundError.toString, error.message)
      case e: ServiceError             => ServerError(ServerError.toString, e.message)
    }
}
