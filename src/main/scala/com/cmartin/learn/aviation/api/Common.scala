package com.cmartin.learn.aviation.api

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

  def handleError(error: DomainError): OutputError = ???
}