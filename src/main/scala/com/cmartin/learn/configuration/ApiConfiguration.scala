package com.cmartin.learn.configuration

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteConcatenation._
import com.cmartin.learn.api.{ActuatorApi, AircraftApi, Json4sApi, SwaggerApi, TransferApi}

trait ApiConfiguration {

  lazy val serverAddress: String = "localhost"
  lazy val serverPort: Int       = 8080

  lazy val routes: Route =
    ActuatorApi.route ~
      TransferApi.routes ~
      AircraftApi.routes ~
      Json4sApi.routes ~
      SwaggerApi.route
}
