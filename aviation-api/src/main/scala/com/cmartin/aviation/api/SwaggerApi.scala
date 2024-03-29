package com.cmartin.aviation.api

import akka.http.scaladsl.server.Route
import sttp.apispec.openapi._
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait SwaggerApi {

  lazy val route: Route =
    AkkaHttpServerInterpreter()
      .toRoute(swaggerEndpoints)

  // add endpoints to the list for swagger documentation
  private lazy val swaggerEndpoints =
    SwaggerInterpreter().fromEndpoints[Future](endpoints, info)

  private lazy val info: Info =
    Info("Tapir Learning Service API", "1.0.0-SNAPSHOT", Some("Researching about Tapir library"))

  private lazy val endpoints = List(
    ActuatorEndpoint.healthEndpoint,
    CountryEndpoints.getByCodeEndpoint,
    CountryEndpoints.getAllEndpoint,
    CountryEndpoints.postEndpoint,
    CountryEndpoints.putEndpoint,
    CountryEndpoints.deleteEndpoint,
    AirportEndpoints.getByIataCodeEndpoint,
    AirportEndpoints.postEndpoint,
    AirportEndpoints.deleteEndpoint,
    AirlineEndpoints.getByIataCodeEndpoint,
    AirlineEndpoints.postEndpoint,
    AirlineEndpoints.deleteEndpoint
  )
}

object SwaggerApi extends SwaggerApi
