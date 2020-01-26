package com.cmartin.learn.api

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteConcatenation._
import sttp.tapir.server.akkahttp._

import scala.concurrent.Future

trait TransferApi {
  lazy val routes =
    getRoute ~
      postRoute ~
      getComOutputRoute ~
      getShaOutputRoute ~
      getACEntityRoute // ~ add more routes

  // tapir endpoint description to akka routes via .toRoute function
  lazy val getRoute: Route =
    TransferEndpoint.getTransferEndpoint.toRoute { _ =>
      Future.successful(Right(TransferEndpoint.transferExample)) // simulating business logic function
    }

  lazy val postRoute: Route =
    TransferEndpoint.postTransferEndpoint.toRoute { transfer =>
      Future.successful(Right(transfer.copy(id = Some(scala.util.Random.nextLong(2048)))))
    }

  lazy val getACEntityRoute: Route =
    TransferEndpoint.getACEntityEndpoint.toRoute { _ =>
      Future.successful(Right(TransferEndpoint.acEntityExample))
    }


  lazy val getComOutputRoute: Route =
    TransferEndpoint.getComOutputEndpoint.toRoute { _ =>
      Future.successful(Right(ApiModel.ComOut))
    }

  lazy val getShaOutputRoute: Route =
    TransferEndpoint.getShaOutputEndpoint.toRoute { _ =>
      Future.successful(Right(ApiModel.ShaOut))
    }


}

object TransferApi extends TransferApi

