package com.cmartin.learn.configuration

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteConcatenation._
import akka.http.scaladsl.server.directives.DebuggingDirectives
import com.cmartin.learn.api._

trait ApiConfiguration {

  lazy val serverAddress: String = "localhost"
  lazy val serverPort: Int       = 8080

  lazy val routes: Route =
    DebuggingDirectives.logRequestResult("route-logger") {
      ActuatorApi.route ~
        TransferApi.routes ~
        AircraftApi.routes ~
        Json4sApi.routes ~
        SwaggerApi.route
    }
}
