package com.cmartin.aviation.api

import akka.http.scaladsl.server.Route
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

import scala.concurrent.Future

trait ActuatorApi {
  import ActuatorEndpoint._

  lazy val route: Route =
    AkkaHttpServerInterpreter()
      .toRoute(ActuatorEndpoint.healthEndpoint)(_ =>
        Future.successful(
          Right(BuildInfo.toView)
        )
      )
}

object ActuatorApi extends ActuatorApi {}
