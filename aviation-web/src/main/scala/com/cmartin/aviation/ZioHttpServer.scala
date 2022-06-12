package com.cmartin.aviation

import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zhttp.http._
import zhttp.http.middleware.HttpMiddleware
import zhttp.service.Server
import zio._

import java.io.IOException

import ApiLayer.SwaggerDocs.swaggerEndpoints

object ZioHttpServer
    extends ZIOAppDefault {

  val routeAspects = Middleware.debug ++ Middleware.timeout(5.seconds) // ++ loggingAspect

  val docsHttp = Http.collect[Request] {
    case Method.GET -> !! / "swagger" => Response.redirect("/docs/index.html")

    case Method.GET -> !! / "error" => Response.fromHttpError(HttpError.BadRequest())
  }

  val routes =
    ZioHttpInterpreter().toHttp(
      swaggerEndpoints ++ ApiLayer.CountryEndpoints.serverEndpoints
    ) ++ docsHttp @@ Middleware.debug // @@ routeAspects

  // val managedErrorRoutes = ??? // mapError or catchAll

  override def run =
    (
      Server
        .start(8080, routes)
        .exitCode
    ).provide(zio.Console.live, zio.Clock.live)
}
