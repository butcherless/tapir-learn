package com.cmartin.aviation.api

import com.cmartin.aviation.domain.Model._
import zio.IO

import BaseEndpoint._
import Model._

object Common {
  val runtime = zio.Runtime.default
  def run[A](program: IO[DomainError, A]): RouteResponse[A] = {
    runtime.unsafeRunToFuture(
      program
        .mapError(handleError)
        .either
    )
  }

  // handle only ApiError, remove DomainError
  // map ServiceError to ApiError, default case
  def handleError(error: DomainError): OutputError = ???
}
