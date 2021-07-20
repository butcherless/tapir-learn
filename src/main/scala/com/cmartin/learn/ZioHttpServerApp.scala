package com.cmartin.learn

import akka.http.javadsl.Http
import com.cmartin.learn.api.ZioSwaggerApi
import com.cmartin.learn.apizio.ActuatorApi
import zhttp.http
import zhttp.http._
import zhttp.service.Server
import zhttp.service._
import zio._
/*
  https://doc.akka.io/docs/akka-http/current/server-side/graceful-termination.html
 */
object ZioHttpServerApp extends App {

  val routes =
    ActuatorApi.healthRoute <>
      ActuatorApi.swaggerVersionRoute <>
      ZioSwaggerApi.route

  override def run(args: List[String]): URIO[ZEnv, ExitCode] = {

    Server.start(8081, routes).exitCode
  }
}
