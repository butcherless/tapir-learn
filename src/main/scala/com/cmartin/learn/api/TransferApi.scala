package com.cmartin.learn.api

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteConcatenation._
import sttp.tapir.server.akkahttp._

import scala.concurrent.Future

trait TransferApi {
  lazy val routes = getRoute ~ getACEntityRoute // ~ add more routes

  // tapir endpoint description to akka routes via .toRoute function
  lazy val getRoute: Route =
    TransferEndpoint.getTransferEndpoint.toRoute { _ =>
      Future.successful(Right(ApiModel.transferExample)) // simulating business logic function
    }

  lazy val getACEntityRoute: Route =
    TransferEndpoint.getACEntityEndpoint.toRoute { _ =>
      Future.successful(Right(ApiModel.acEntityExample))
      Future.successful(Right(ApiModel.ShaOut))
    }
}

object TransferApi extends TransferApi

