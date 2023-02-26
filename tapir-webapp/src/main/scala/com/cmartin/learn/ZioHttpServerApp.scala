package com.cmartin.learn

import com.cmartin.learn.apizio.ActuatorApi
import zio._
import zio.http.{Server, ServerConfig}

import java.net.InetSocketAddress
class ZioHttpServerApp
    extends ZIOAppDefault {

  val routes =
    ActuatorApi.healthRoute // <>
  // ZioSwaggerApi.route

  override def run =
    Server.serve(routes.withDefaultErrorResponse)
      .provide(
        ServerConfig.live(ServerConfig()
          .binding(new InetSocketAddress(8090))) >>> Server.default
      ).exitCode

}
