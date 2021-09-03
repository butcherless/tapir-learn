package com.cmartin.aviation

import zio.Runtime
import zio.ULayer
import zio.ZEnv
import zio.logging._
import zio.logging.slf4j.Slf4jLogger

object Commons {
  val runtime: Runtime[ZEnv] =
    Runtime.default

  val loggingEnv: ULayer[Logging] =
    Slf4jLogger.make((_, message) => message)
}
