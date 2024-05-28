package com.cmartin.aviation

import com.cmartin.aviation.ApiLayer.{CountryEndpoints, SwaggerDocs}
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zio._
import zio.http.{HttpApp, Middleware, Server}

object ZioHttpServer
    extends ZIOAppDefault {

  val routeAspects = Middleware.debug ++ Middleware.timeout(5.seconds) // ++ loggingAspect

  val routes =
    ZioHttpInterpreter().toHttp(
      //SwaggerDocs.swaggerEndpoints ++
        CountryEndpoints.serverEndpoints
    ) // @@ Middleware.debug // @@ routeAspects

  // val managedErrorRoutes = ??? // mapError or catchAll

  // verify: http://localhost:8081/docs/
  override def run =
    Server.serve(routes)
      .exitCode
      .provide(
        Server
          .defaultWithPort(8081)
      )
}
