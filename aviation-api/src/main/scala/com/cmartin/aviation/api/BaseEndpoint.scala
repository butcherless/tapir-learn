package com.cmartin.aviation.api

import com.cmartin.aviation.api.Model._
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._

import scala.concurrent.Future

trait BaseEndpoint {

  type RouteResponse[T] = Future[Either[OutputError, T]]

  lazy val apiText: String                      = "api"
  lazy val apiVersionText: String               = "v1.0"
  lazy val baseApiResource: EndpointInput[Unit] = apiText / apiVersionText
  lazy val baseApiPath: String                  = s"$apiText/$apiVersionText"

  lazy val baseEndpoint: PublicEndpoint[Unit, Unit, Unit, Any] =
    endpoint
      .in(baseApiResource)
      .name(apiVersionText)
      .description("Aviation REST API")

  val badRequestMapping: EndpointOutput.OneOfVariant[BadRequestError] =
    oneOfVariantFromMatchType(StatusCode.BadRequest, jsonBody[BadRequestError])

  val notFoundMapping: EndpointOutput.OneOfVariant[NotFoundError] =
    oneOfVariantFromMatchType(StatusCode.NotFound, jsonBody[NotFoundError])

  val conflictMapping: EndpointOutput.OneOfVariant[ConflictError] =
    oneOfVariantFromMatchType(StatusCode.Conflict, jsonBody[ConflictError])

  val internalErrorMapping: EndpointOutput.OneOfVariant[NotFoundError] =
    oneOfVariantFromMatchType(StatusCode.NotFound, jsonBody[NotFoundError])

  val defaultMapping: EndpointOutput.OneOfVariant[UnknownError] =
    oneOfDefaultVariant(jsonBody[UnknownError])

  def buildContentLocation(resourcePath: String, resourceId: String): String = {
    s"/$baseApiPath/$resourcePath/$resourceId"
  }

}

object BaseEndpoint extends BaseEndpoint
