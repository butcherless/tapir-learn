package com.cmartin.learn.api

import com.cmartin.learn.api.ApiModel.BuildInfo
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir.json.circe._
import sttp.tapir.{Endpoint, _}

trait ActuatorEndpoint {
  type HealthInfo = BuildInfo

  //json encode/decode via circe.generic.auto
  lazy val healthEndpoint: Endpoint[Unit, StatusCode, HealthInfo, Nothing] =
    endpoint.get
      .in(CommonEndpoint.baseEndpointInput / "health")
      .name("health-endpoint")
      .description("Health Check Endpoint")
      .out(jsonBody[HealthInfo].example(ApiModel.buildInfo))
      .errorOut(statusCode)
}

object ActuatorEndpoint extends ActuatorEndpoint
