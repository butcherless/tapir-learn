package com.cmartin.learn.configuration

import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.server.RouteConcatenation._
import org.apache.pekko.http.scaladsl.server.directives.DebuggingDirectives
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
