package com.cmartin.learn.api

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteConcatenation._
import com.cmartin.learn.api.AircraftEndpoint.{apiAircraftLVLExample, apiAircraftMIGExample}
import com.cmartin.learn.api.Model.AircraftDto
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.akkahttp._

import scala.concurrent.Future

trait AircraftApi {

  lazy val routes: Route =
    getRoute ~
      getSeqRoute ~
      postRoute

  lazy val getAircraftEndpoint: Endpoint[Option[Int], StatusCode, AircraftDto, Any] =
    endpoint.get
      .in(CommonEndpoint.baseEndpointInput / "aircrafts")
      .in(limitQuery.example(Some(20)))
      .name("get-aircraft-endpoint")
      .description("Retrieve aircraft endpoint")
      .out(jsonBody[AircraftDto].example(apiAircraftMIGExample))
      .errorOut(statusCode)

  lazy val getAircraftSeqEndpoint: Endpoint[Unit, StatusCode, Seq[AircraftDto], Any] =
    endpoint.get
      .in(CommonEndpoint.baseEndpointInput / "aircraft-list")
      .name("get-aircraft-list-endpoint")
      .description("Retrieve aircraft list endpoint")
      .out(jsonBody[Seq[AircraftDto]].example(Seq(apiAircraftMIGExample, apiAircraftLVLExample)))
      .errorOut(statusCode)

  lazy val postAircraftEndpoint: Endpoint[AircraftDto, StatusCode, AircraftDto, Any] =
    endpoint.post
      .in(CommonEndpoint.baseEndpointInput / "aircrafts")
      .name("post-aircraft-endpoint")
      .description("Create Aircraft Endpoint")
      .in(jsonBody[AircraftDto].example(apiAircraftMIGExample))
      .out(jsonBody[AircraftDto].example(apiAircraftMIGExample))
      .errorOut(statusCode)

  lazy val getRoute: Route =
    getAircraftEndpoint.toRoute { _ =>
      Future.successful(Right(apiAircraftMIGExample))
    }

  lazy val getSeqRoute: Route =
    getAircraftSeqEndpoint.toRoute { _ =>
      Future.successful(
        Right(Seq(apiAircraftMIGExample.copy(id = Some(1234)), apiAircraftLVLExample.copy(id = Some(5678))))
      )
    }

  lazy val postRoute: Route =
    postAircraftEndpoint.toRoute { aircraft =>
      Future.successful(Right(aircraft.copy(id = Some(1234L))))
    }

  val limitQuery =
    query[Option[Int]]("limit")
}

object AircraftApi extends AircraftApi {}
