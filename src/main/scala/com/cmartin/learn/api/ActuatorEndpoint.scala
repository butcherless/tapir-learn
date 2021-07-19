package com.cmartin.learn.api

import com.cmartin.learn.api.Model.BuildInfoDto
import com.cmartin.learn.apizio.ActuatorApi.Artifact
import com.cmartin.learn.domain.ApiConverters
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir.Endpoint
import sttp.tapir._
import sttp.tapir.endpoint
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._

trait ActuatorEndpoint extends ApiCodecs {

  import ActuatorEndpoint._

  type HealthInfo = BuildInfoDto

  //json encode/decode via circe.generic.auto
  lazy val healthEndpoint: Endpoint[Unit, StatusCode, HealthInfo, Any] =
    endpoint.get
      .in(CommonEndpoint.baseEndpointInput / "health")
      .name("health-endpoint")
      .description("Health Check Endpoint")
      .out(jsonBody[HealthInfo].example(ApiConverters.modelToApi()))
      .errorOut(statusCode)

  //TODO remove after ApiZio POC
  lazy val swaggerVersionEndpoint: Endpoint[Unit, StatusCode, Artifact, Any] =
    endpoint.get
      .in(CommonEndpoint.baseEndpointInput / "swaggerVersion")
      .name("swagger-version")
      .description("Retrieve the Swagger UI version installed inServer")
      .out(jsonBody[Artifact].example(artifact))
      .errorOut(statusCode)
}

object ActuatorEndpoint extends ActuatorEndpoint {
  val artifact = Artifact("org.webjars", "swagger-ui", "3.51.1")
}
