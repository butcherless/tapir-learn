package com.cmartin.aviation

import zio.Runtime
import zio.ZEnv
import zio._
import zio.logging._
import zio.logging.slf4j.Slf4jLogger

import domain.Model._

object Commons {

  type ServiceResponseOld[A] = ZIO[Logging, ServiceError, A]
  type ServiceResponse[A] = IO[ServiceError, A]
  type RepositoryResponse[A] = ZIO[Logging, RepositoryError, A]

  val runtime: Runtime[ZEnv] =
    Runtime.default

  val loggingEnv: ZLayer[Any, Nothing, Has[Logging]] =
    Slf4jLogger.make((_, message) => message)
      .map(Has(_))
}
