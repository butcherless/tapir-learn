package com.cmartin.learn

import com.cmartin.learn.api.ZioSwaggerApi
import com.cmartin.learn.apizio.ActuatorApi
import zhttp.http
import zhttp.service.Server
import zio._

/*
  https://doc.akka.io/docs/akka-http/current/server-side/graceful-termination.html
 */
object ZioHttpServerApp
    extends App {

  val routes =
    ActuatorApi.healthRoute <>
      ZioSwaggerApi.route

  override def run(args: List[String]): URIO[ZEnv, ExitCode] = {

    Server.start(8081, routes).exitCode
  }

}
