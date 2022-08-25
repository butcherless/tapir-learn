package com.cmartin.aviation.api

import akka.http.scaladsl.server.Route
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
import zio.Runtime.{default => runtime}
import zio.{Unsafe, ZIO}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ActuatorApi {
  import ActuatorEndpoint._

  lazy val route: Route =
    AkkaHttpServerInterpreter()
      .toRoute(
        ActuatorEndpoint.healthEndpoint.serverLogicSuccess(_ =>
          doGetLogic()
        )
      )

  /* TODO add logger & metrics aspect ZIO v2
   */
  private def doGetLogic(): Future[Model.BuildInfoView] =
    Unsafe.unsafe { implicit u =>
      runtime.unsafe.runToFuture(ZIO.succeed((BuildInfo.toView)))
    }

}

object ActuatorApi extends ActuatorApi {}
