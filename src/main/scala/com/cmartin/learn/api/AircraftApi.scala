package com.cmartin.learn.api

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteConcatenation._
import com.cmartin.learn.api.ApiModel.AircraftDto
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir.json.circe._
import sttp.tapir.server.akkahttp._
import sttp.tapir.{Endpoint, _}

import scala.concurrent.Future

trait AircraftApi {

  lazy val routes =
    getRoute ~
      getSeqRoute ~
      postRoute

  //TODO move to AircraftEndpoint companion object
  val apiAircraftExample  = AircraftDto("ec-nei", 1, "Boeing788", None)
  val apiAircraft2Example = AircraftDto("ec-lvl", 1, "Airbus333", None)

  lazy val getAircraftEndpoint: Endpoint[Unit, StatusCode, AircraftDto, Nothing] =
    endpoint.get
      .in(CommonEndpoint.baseEndpointInput / "aircrafts")
      .name("get-aircraft-endpoint")
      .description("Retrieve aircraft endpoint")
      .out(jsonBody[AircraftDto].example(apiAircraftExample))
      .errorOut(statusCode)

  lazy val getAircraftSeqEndpoint: Endpoint[Unit, StatusCode, Seq[AircraftDto], Nothing] =
    endpoint.get
      .in(CommonEndpoint.baseEndpointInput / "aircraft-list")
      .name("get-aircraft-list-endpoint")
      .description("Retrieve aircraft list endpoint")
      .out(jsonBody[Seq[AircraftDto]].example(Seq(apiAircraftExample, apiAircraft2Example)))
      .errorOut(statusCode)

  lazy val postAircraftEndpoint: Endpoint[AircraftDto, StatusCode, AircraftDto, Nothing] =
    endpoint.post
      .in(CommonEndpoint.baseEndpointInput / "aircrafts")
      .name("post-aircraft-endpoint")
      .description(("Create Aircraft Endpoint"))
      .in(jsonBody[AircraftDto].example(apiAircraftExample))
      .out(jsonBody[AircraftDto].example(apiAircraftExample))
      .errorOut(statusCode)

  lazy val getRoute: Route =
    getAircraftEndpoint.toRoute { _ =>
      Future.successful(Right(apiAircraftExample))
    }

  lazy val getSeqRoute: Route =
    getAircraftSeqEndpoint.toRoute { _ =>
      Future.successful(Right(Seq(apiAircraftExample.copy(id = Some(1234)), apiAircraft2Example.copy(id = Some(5678)))))
    }

  lazy val postRoute: Route =
    postAircraftEndpoint.toRoute { aircraft =>
      Future.successful(Right(aircraft.copy(id = Some(1234L))))
    }
}

object AircraftApi extends AircraftApi {}
