package com.cmartin.learn.api

import org.apache.pekko.http.scaladsl.server.Route
import com.cmartin.learn.domain.ApiConverters
import sttp.tapir.server.pekkohttp.PekkoHttpServerInterpreter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ActuatorApi {

  // tapir endpoint description to akka routes via .toRoute function
  lazy val route: Route =
    PekkoHttpServerInterpreter()
      .toRoute(
        ActuatorEndpoint.healthEndpoint.serverLogicSuccess { _ =>
          Future.successful(ApiConverters.modelToApi())
        }
      )
}

object ActuatorApi extends ActuatorApi
