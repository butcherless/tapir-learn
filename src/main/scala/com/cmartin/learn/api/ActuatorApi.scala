package com.cmartin.learn.api

import akka.http.scaladsl.server.Route
import com.cmartin.learn.domain.{ApiConverters, DomainModel}
import sttp.tapir.server.akkahttp._

import scala.concurrent.Future

trait ActuatorApi {

  // tapir endpoint description to akka routes via .toRoute function
  lazy val route: Route =
    ActuatorEndpoint.healthEndpoint.toRoute { _ =>
      Future.successful {
        val buildInfo = DomainModel.buildInfo()
        Right(ApiConverters.modelToApi(buildInfo))
      }
    }

}

object ActuatorApi extends ActuatorApi
