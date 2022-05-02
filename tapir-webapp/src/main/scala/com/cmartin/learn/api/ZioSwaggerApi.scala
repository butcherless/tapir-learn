package com.cmartin.learn.api

import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.Info
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.swagger.SwaggerUI
import zhttp.http.{Http, Request, Response}
import zio.Task

trait ZioSwaggerApi {

  lazy val route: Http[Any, Throwable, Request, Response] =
    ZioHttpInterpreter()
      .toHttp(SwaggerUI[Task](docsAsYaml))

  // add endpoints to the list for swagger documentation
  private lazy val docsAsYaml: String =
    OpenAPIDocsInterpreter()
      .toOpenAPI(endpoints, info)
      .toYaml

  private lazy val info = Info("Tapir Learning Service API", "1.0.0-SNAPSHOT", Some("Researching about Tapir library"))

  private lazy val endpoints = Seq(
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

object ZioSwaggerApi extends ZioSwaggerApi
