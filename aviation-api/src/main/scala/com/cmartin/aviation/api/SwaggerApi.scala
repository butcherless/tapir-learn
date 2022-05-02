package com.cmartin.aviation.api

import akka.http.scaladsl.server.Route
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.Info
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
import sttp.tapir.swagger.SwaggerUI

import scala.concurrent.Future

trait SwaggerApi {

  lazy val route: Route =
    AkkaHttpServerInterpreter()
      .toRoute(SwaggerUI[Future](docsAsYaml))

  // add endpoints to the list for swagger documentation
  private lazy val docsAsYaml: String =
    OpenAPIDocsInterpreter()
      .toOpenAPI(endpoints, info)
      .toYaml

  private lazy val info = Info("Tapir Learning Service API", "1.0.0-SNAPSHOT", Some("Researching about Tapir library"))

  private lazy val endpoints = Seq(
    ActuatorEndpoint.healthEndpoint,
    CountryEndpoints.getByCodeEndpoint,
    CountryEndpoints.postEndpoint,
    CountryEndpoints.putEndpoint,
    CountryEndpoints.deleteEndpoint
  )
}

object SwaggerApi extends SwaggerApi
