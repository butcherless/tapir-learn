package com.cmartin.learn.api

import sttp.tapir._

object CommonEndpoint {

  val API_TEXT = "api"
  val API_VERSION = "v1.0"
  val BASE_API = s"$API_TEXT/$API_VERSION"

  val baseEndpointInput: EndpointInput[Unit] = API_TEXT / API_VERSION


}
