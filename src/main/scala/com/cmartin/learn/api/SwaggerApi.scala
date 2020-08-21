package com.cmartin.learn.api

import akka.http.scaladsl.server.Route
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.circe.yaml._

trait SwaggerApi {

  // add endpoints to the list for swagger documentation
  lazy val docsAsYaml: String = List(
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
  ).toOpenAPI("Tapir Learning Service API", "1.0.0-SNAPSHOT").toYaml

  //
  lazy val route: Route =
    //pathPrefix(API_TEXT / API_VERSION) {
    new CustomSwaggerAkka(docsAsYaml).routes
  //}
}

object SwaggerApi extends SwaggerApi
