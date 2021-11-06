package com.cmartin.learn.apizio

import com.cmartin.learn.api.ActuatorEndpoint
import com.cmartin.learn.domain.ApiConverters
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.ztapir._
import zio.ZIO

trait ActuatorApi {

  // tapir endpoint description to zio-http routes via .toHttp function
  lazy val healthRoute =
    ZioHttpInterpreter().toHttp(
      ActuatorEndpoint.healthEndpoint.zServerLogic { _ =>
        ZIO.succeed(ApiConverters.modelToApi())
      }
    )
}

object ActuatorApi extends ActuatorApi
