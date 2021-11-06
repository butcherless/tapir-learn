package com.cmartin.aviation.api

import akka.http.scaladsl.server.Route
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

import scala.concurrent.Future

trait ActuatorApi {
  import ActuatorEndpoint._

  lazy val route: Route =
    AkkaHttpServerInterpreter()
      .toRoute(
        ActuatorEndpoint.healthEndpoint.serverLogicSuccess(_ =>
          Future.successful((BuildInfo.toView))
        )
      )
}

object ActuatorApi extends ActuatorApi {}
