package com.cmartin.learn.api

import akka.http.scaladsl.server.Route
import com.cmartin.learn.api.CommonEndpoint.BASE_API
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.Info
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.swagger.akkahttp.SwaggerAkka

trait SwaggerApi {

  lazy val route: Route = {
    new SwaggerAkka(docsAsYaml, s"$BASE_API/docs").routes
  }

  // add endpoints to the list for swagger documentation
  private lazy val docsAsYaml: String =
    OpenAPIDocsInterpreter()
      .toOpenAPI(endpoints, info)
      .toYaml

  private lazy val info = Info("Tapir Learning Service API", "1.0.0-SNAPSHOT", Some("Researching about Tapir library"))

  private lazy val endpoints = List(
    ActuatorEndpoint.healthEndpoint,
    TransferEndpoint.getTransferEndpoint,
    TransferEndpoint.getFilteredTransferEndpoint,
    TransferEndpoint.getWithHeaderTransferEndpoint,
    TransferEndpoint.getComOutputEndpoint,
    TransferEndpoint.getShaOutputEndpoint,
    TransferEndpoint.getACEntityEndpoint,
    TransferEndpoint.postTransferEndpoint,
    TransferEndpoint.postJsonEndpoint,
    AircraftApi.getAircraftEndpoint,
    AircraftApi.getAircraftSeqEndpoint,
    AircraftApi.getAircraftTypeEndpoint,
    AircraftApi.postAircraftEndpoint,
    Json4sApi.getAircraftEndpoint,
    Json4sApi.getJsonEndpoint,
    Json4sApi.postJsonEndpoint,
    Json4sApi.postEntityEndpoint
  )
}

object SwaggerApi extends SwaggerApi
