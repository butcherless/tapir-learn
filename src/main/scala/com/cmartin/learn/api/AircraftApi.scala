package com.cmartin.learn.api

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteConcatenation._
import com.cmartin.learn.api.ApiModel.ApiAircraft
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir.json.circe._
import sttp.tapir.server.akkahttp._
import sttp.tapir.{Endpoint, _}

import scala.concurrent.Future

trait AircraftApi {

  lazy val routes =
    getRoute ~
      postRoute


  //TODO move to AircraftEndpoint companion object
  val apiAircraftExample = ApiAircraft("ec-nei", 1, "Boeing788", None)

  lazy val getAircraftEndpoint: Endpoint[Unit, StatusCode, ApiAircraft, Nothing] =
    endpoint
      .get
      .in(CommonEndpoint.baseEndpointInput / "aircrafts")
      .name("get-aircraft-endpoint")
      .description("Retrieve aircraft endpoint")
      .out(jsonBody[ApiAircraft].example(apiAircraftExample))
      .errorOut(statusCode)

  lazy val postAircraftEndpoint: Endpoint[ApiAircraft, StatusCode, ApiAircraft, Nothing] =
    endpoint
      .post
      .in(CommonEndpoint.baseEndpointInput / "aircrafts")
      .name("post-aircraft-endpoint")
      .description(("Create Aircraft Endpoint"))
      .in(jsonBody[ApiAircraft].example(apiAircraftExample))
      .out(jsonBody[ApiAircraft].example(apiAircraftExample))
      .errorOut(statusCode)


  lazy val getRoute: Route =
    getAircraftEndpoint.toRoute { _ =>
      Future.successful(Right(apiAircraftExample))
    }

  lazy val postRoute: Route =
    postAircraftEndpoint.toRoute { aircraft =>
      Future.successful(Right(aircraft.copy(id = Some(1234L))))
    }
}

object AircraftApi extends AircraftApi {
}