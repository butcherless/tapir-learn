package com.cmartin.learn.api

import scala.concurrent.Future

import akka.http.scaladsl.server.Route
import com.cmartin.learn.domain.ApiConverters
import sttp.tapir.server.akkahttp._

trait ActuatorApi {

  // tapir endpoint description to akka routes via .toRoute function
  lazy val route: Route =
    ActuatorEndpoint.healthEndpoint.toRoute { _ =>
      Future.successful {
        Right(ApiConverters.modelToApi())
      }
    }
}

object ActuatorApi extends ActuatorApi
