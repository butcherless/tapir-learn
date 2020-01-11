package com.cmartin.learn.api

import akka.http.scaladsl.server.Route
import sttp.tapir.server.akkahttp._

import scala.concurrent.Future

trait TransferApi {
  lazy val routes = getRoute // ~ add more routes

  lazy val getRoute: Route =
    TransferEndpoint.getTransferEndpoint.toRoute { _ =>
      Future.successful(Right(ApiModel.transferExample)) // simulating business logic function
    }
}

object TransferApi extends TransferApi

