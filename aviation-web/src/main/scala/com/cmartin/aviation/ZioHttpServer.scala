package com.cmartin.aviation

import com.cmartin.aviation.ApiLayer.{CountryEndpoints, SwaggerDocs}
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zhttp.http._
import zhttp.service.Server
import zio._

object ZioHttpServer
    extends ZIOAppDefault {

  val routeAspects = Middleware.debug ++ Middleware.timeout(5.seconds) // ++ loggingAspect

  val routes =
    ZioHttpInterpreter().toHttp(
      SwaggerDocs.swaggerEndpoints ++
        CountryEndpoints.serverEndpoints
    ) @@ Middleware.debug // @@ routeAspects

  // val managedErrorRoutes = ??? // mapError or catchAll

  override def run =
    (
      Server
        .start(8080, routes)
        .exitCode
    ).provide(
      zio.Console.live,
      zio.Clock.live
    )
}
