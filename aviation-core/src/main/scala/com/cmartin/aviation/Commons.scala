package com.cmartin.aviation

import zio.Runtime
import zio.ULayer
import zio.ZEnv
import zio.ZIO
import zio.logging._
import zio.logging.slf4j.Slf4jLogger

import domain.Model._

object Commons {

  type ServiceResponse[A] = ZIO[Logging, ServiceError, A]
  type RepositoryResponse[A] = ZIO[Logging, RepositoryError, A]

  val runtime: Runtime[ZEnv] =
    Runtime.default

  val loggingEnv: ULayer[Logging] =
    Slf4jLogger.make((_, message) => message)
}
