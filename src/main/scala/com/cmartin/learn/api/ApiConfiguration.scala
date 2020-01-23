package com.cmartin.learn.api

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteConcatenation._

trait ApiConfiguration {

  lazy val routes: Route =
    ActuatorApi.route ~
      TransferApi.routes ~
      SwaggerApi.route
}
