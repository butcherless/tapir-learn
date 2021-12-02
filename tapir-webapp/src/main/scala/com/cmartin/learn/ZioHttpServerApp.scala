package com.cmartin.learn

import com.cmartin.learn.api.ZioSwaggerApi
import com.cmartin.learn.apizio.ActuatorApi
import zhttp.http
import zhttp.service.Server
import zio._

object ZioHttpServerApp
    extends ZIOAppDefault {

  val routes =
    ActuatorApi.healthRoute <>
      ZioSwaggerApi.route

  def run =
    for {
      code <- Server.start(8081, routes).exitCode
    } yield code

}
