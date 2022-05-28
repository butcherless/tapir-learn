package com.cmartin.learn.api

trait ZioSwaggerApi
/*
import sttp.tapir.docs.openapi._
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.swagger.SwaggerUI
import zhttp.http.{Http, Request, Response}
import zio.Task
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import scala.concurrent.Future
import sttp.tapir.ztapir._
import sttp.tapir.docs.openapi._
import sttp.apispec.openapi.circe.yaml._
import sttp.apispec.openapi._
import zio.UIO
trait ZioSwaggerApi {
  lazy val route: Http[Any, Throwable, Request, Response] =
    ZioHttpInterpreter()
      .toHttp(swaggerEndpoints)

  // add endpoints to the list for swagger documentation
  private lazy val swaggerEndpoints =
    SwaggerInterpreter().fromEndpoints(endpoints, info)

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
 */
object ZioSwaggerApi
    extends ZioSwaggerApi
