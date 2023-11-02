package com.cmartin.learn

import com.cmartin.learn.apizio.ActuatorApi
import zio._
import zio.http.Server
class ZioHttpServerApp
    extends ZIOAppDefault {

  val routes =
    ActuatorApi.healthRoute // <>
  // ZioSwaggerApi.route

  override def run: URIO[Any, ExitCode] = {
    Server.serve(routes)
      .provide(
        ZLayer.succeed(Server.Config.default.port(8090)),
        Server.live
      )
      .exitCode
  }

}
