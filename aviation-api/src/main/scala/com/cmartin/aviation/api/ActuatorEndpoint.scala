package com.cmartin.aviation.api

import com.cmartin.aviation.api.BaseEndpoint.baseEndpoint
import com.cmartin.aviation.api.Model.BuildInfoView
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir.Endpoint
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.statusCode

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

trait ActuatorEndpoint {
  import ActuatorEndpoint._

  lazy val healthEndpoint: Endpoint[Unit, StatusCode, BuildInfoView, Any] =
    baseEndpoint
      .get
      .in("health")
      .name("health-endpoint")
      .description("Health Check Endpoint")
      .out(jsonBody[BuildInfoView].example(BuildInfo.toView))
      .errorOut(statusCode)
}

object ActuatorEndpoint extends ActuatorEndpoint {
  implicit class BuildInfoToView(info: BuildInfo.type) {
    def toView: BuildInfoView = BuildInfoView(
      name = info.name,
      version = info.version,
      scalaVersion = info.scalaVersion,
      sbtVersion = info.sbtVersion,
      gitCommit = info.gitCommit,
      builtAtMillis = LocalDateTime
        .ofInstant(
          Instant.ofEpochMilli(info.builtAtMillis),
          ZoneId.systemDefault()
        )
    )
  }

}
