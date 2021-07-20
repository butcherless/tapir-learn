package com.cmartin.learn.apizio

import com.cmartin.learn.api.ActuatorEndpoint
import com.cmartin.learn.domain.ApiConverters
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zio.ZIO

trait ActuatorApi {

  // tapir endpoint description to zio-http routes via .toHttp function
  lazy val healthRoute =
    ZioHttpInterpreter()
      .toHttp(ActuatorEndpoint.healthEndpoint) { _ =>
        ZIO.succeed(Right(ApiConverters.modelToApi()))
      }
}

object ActuatorApi extends ActuatorApi
