package com.cmartin.learn.apizio

import com.cmartin.learn.api.ActuatorEndpoint
import com.cmartin.learn.domain.ApiConverters
import sttp.model.StatusCode
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zio.Task
import zio.ZIO

import java.util.Properties

trait ActuatorApi {
  import ActuatorApi.Artifact
  import ActuatorApi.getSwaggerVersion

  // tapir endpoint description to zio-http routes via .toHttp function
  lazy val healthRoute =
    ZioHttpInterpreter()
      .toHttp(ActuatorEndpoint.healthEndpoint)(_ => ZIO.succeed(Right(ApiConverters.modelToApi())))

  def converter(t: Task[Artifact]) = {
    t.fold(
      _ => Left(StatusCode.InternalServerError),
      Right(_)
    )
  }

  lazy val swaggerVersionRoute =
    ZioHttpInterpreter()
      .toHttp(ActuatorEndpoint.swaggerVersionEndpoint)(_ => converter(getSwaggerVersion())
      //.mapError(_ => Left(StatusCode.InternalServerError))
      )
}

object ActuatorApi extends ActuatorApi {

  case class Artifact(groupId: String, artifactId: String, version: String)

  def getSwaggerVersion(): Task[Artifact] =
    Task.effect {
      val ps            = new Properties()
      val pomProperties = getClass.getResourceAsStream("/META-INF/maven/org.webjars/swagger-ui/pom.properties")
      try ps.load(pomProperties)
      finally pomProperties.close()
      println(ps.toString)
      Artifact(ps.getProperty("groupId"), ps.getProperty("artifactId"), ps.getProperty("version"))
    }

}

// http://localhost:8080/api/v1.0/docs/index.html?url=/api/v1.0/docs/docs.yaml
