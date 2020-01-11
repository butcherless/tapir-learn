package com.cmartin.learn.api

import sttp.tapir._

object CommonEndpoint {

  val API_TEXT = "api"
  val API_VERSION = "v1.0"

  val baseEndpointInput: EndpointInput[Unit] = API_TEXT / API_VERSION
}
