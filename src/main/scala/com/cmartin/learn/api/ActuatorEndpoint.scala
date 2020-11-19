package com.cmartin.learn.api

import java.time.{Clock, LocalDateTime}

import com.cmartin.learn.api.ApiModel.{APP_NAME, APP_VERSION, ApiBuildInfo, BuildInfoDto}
import com.cmartin.learn.domain.ApiConverters
import io.circe.generic.auto._
import sttp.tapir.generic.auto._
import sttp.tapir.server.akkahttp._

import sttp.model.StatusCode
import sttp.tapir.json.circe._
import sttp.tapir.{Endpoint, endpoint, _}

trait ActuatorEndpoint extends ApiCodecs {

  type HealthInfo = BuildInfoDto

  //json encode/decode via circe.generic.auto
  lazy val healthEndpoint: Endpoint[Unit, StatusCode, HealthInfo, Any] =
    endpoint.get
      .in(CommonEndpoint.baseEndpointInput / "health")
      .name("health-endpoint")
      .description("Health Check Endpoint")
      .out(jsonBody[HealthInfo].example(ApiConverters.modelToApi()))
      .errorOut(statusCode)
}

object ActuatorEndpoint extends ActuatorEndpoint {
  val exampleApiBuildInfo: ApiBuildInfo = ApiBuildInfo(
    APP_NAME,
    LocalDateTime.now(Clock.systemDefaultZone()).toString,
    APP_VERSION,
    "Success"
  )

}
