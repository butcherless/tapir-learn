package com.cmartin.aviation.api

import com.cmartin.aviation.api.BaseEndpoint._
import com.cmartin.aviation.api.Model._
import com.cmartin.aviation.domain.Model._
import zio.Runtime.{default => runtime}
import zio.{IO, Unsafe}

object Common {

  type ApiResponse[A]  = IO[ProgramError, A]
  type Api2Response[A] = IO[ProgramError, A]

  // val loggingEnv: ZLayer[Any, Nothing, Logging] =    Slf4jLogger.make((_, message) => message)

  /* leave the pure world to the real impure world
   */
  def run[A](program: ApiResponse[A]): RouteResponse[A] = {
    Unsafe.unsafe { implicit u =>
      runtime.unsafe.runToFuture(
        program
          // .provideLayer(loggingEnv)
          .mapError(handleError)
          .either
      )
    }
  }

  /* leave the pure world to the real impure world
   */
  def run2[A](program: Api2Response[A]): RouteResponse[A] = {
    Unsafe.unsafe { implicit u =>
      runtime.unsafe.runToFuture(
        program
          .mapError(handleError)
          .either
      )
    }
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
