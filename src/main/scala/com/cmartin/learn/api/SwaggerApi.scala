package com.cmartin.learn.api

import akka.http.scaladsl.server.Route
import com.cmartin.learn.api.CommonEndpoint.BASE_API
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.Info
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.swagger.akkahttp.SwaggerAkka

trait SwaggerApi {

  private val endpoints = List(
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
    AircraftApi.postAircraftEndpoint,
    Json4sApi.getAircraftEndpoint,
    Json4sApi.getJsonEndpoint,
    Json4sApi.postJsonEndpoint,
    Json4sApi.postEntityEndpoint
  )

  val info = Info("Tapir Learning Service API", "1.0.0-SNAPSHOT", Some("Researching about Tapir library"))

  // add endpoints to the list for swagger documentation
  lazy val docsAsYaml: String =
    OpenAPIDocsInterpreter
      .toOpenAPI(endpoints, info)
      .toYaml

  lazy val route: Route = {
    new SwaggerAkka(docsAsYaml, s"$BASE_API/docs").routes
  }
}

object SwaggerApi extends SwaggerApi
