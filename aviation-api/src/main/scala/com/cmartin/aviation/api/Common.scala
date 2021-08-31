package com.cmartin.aviation.api

import com.cmartin.aviation.Commons._
import com.cmartin.aviation.domain.Model._
import zio.ZIO
import zio.logging._

import BaseEndpoint._
import Model._

object Common {

  type ApiResponse[A] = ZIO[Logging, ProgramError, A]

  def run[A](program: ApiResponse[A]): RouteResponse[A] = {
    runtime.unsafeRunToFuture(
      program
        .provideLayer(loggingEnv)
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
    }
}
