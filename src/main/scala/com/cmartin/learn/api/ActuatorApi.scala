package com.cmartin.learn.api

import akka.http.scaladsl.server.Route
import com.cmartin.learn.domain.ApiConverters
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

import scala.concurrent.Future

trait ActuatorApi {

  // tapir endpoint description to akka routes via .toRoute function
  lazy val route: Route =
    AkkaHttpServerInterpreter
      .toRoute(ActuatorEndpoint.healthEndpoint)(_ =>
        Future.successful {
          Right(ApiConverters.modelToApi())
        }
      )
}

object ActuatorApi extends ActuatorApi
