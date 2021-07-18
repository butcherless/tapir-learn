package com.cmartin.learn.apizio

import com.cmartin.learn.domain.ApiConverters
import com.cmartin.learn.api.ActuatorEndpoint
import sttp.tapir.server.ziohttp.ZioHttpInterpreter

import scala.concurrent.Future
import zio.ZIO

trait ActuatorApi {

  // tapir endpoint description to zio-http routes via .toHttp function
  lazy val route =
    ZioHttpInterpreter()
      .toHttp(ActuatorEndpoint.healthEndpoint)(_ => ZIO.succeed(Right(ApiConverters.modelToApi())))
}

object ActuatorApi extends ActuatorApi
