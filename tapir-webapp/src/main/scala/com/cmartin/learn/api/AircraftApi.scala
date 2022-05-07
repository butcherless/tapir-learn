package com.cmartin.learn.api

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteConcatenation._
import com.cmartin.learn.api.AircraftEndpoint._
import com.cmartin.learn.api.Model.AircraftType._
import com.cmartin.learn.api.Model._
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait AircraftApi {

  lazy val routes: Route =
    getRoute ~
      getSeqRoute ~
      postRoute ~
      getTypeRoute

  lazy val getAircraftTypeEndpoint: PublicEndpoint[Unit, StatusCode, Seq[AircraftType], Any] =
    endpoint.get
      .in(CommonEndpoint.baseEndpointInput / "aircraft-types")
      .name("get-aircraft-type-endpoint")
      .description("Retrieve aircraft types")
      .out(jsonBody[Seq[AircraftType]].example(Seq(AircraftType.AirbusA320N, AircraftType.Boeing789)))
      .errorOut(statusCode)

  lazy val getTypeRoute: Route =
    AkkaHttpServerInterpreter().toRoute(
      getAircraftTypeEndpoint.serverLogicSuccess { _ =>
        Future.successful(AircraftType.values.toSeq)
      }
    )

  lazy val getAircraftEndpoint: PublicEndpoint[Option[Int], StatusCode, AircraftDto, Any] =
    endpoint.get
      .in(CommonEndpoint.baseEndpointInput / "aircrafts")
      .in(limitQuery.example(Some(20)))
      .name("get-aircraft-endpoint")
      .description("Retrieve aircraft endpoint")
      .out(jsonBody[AircraftDto].example(apiAircraftMIGExample))
      .errorOut(statusCode)

  lazy val getAircraftSeqEndpoint: PublicEndpoint[Unit, StatusCode, Seq[AircraftDto], Any] =
    endpoint.get
      .in(CommonEndpoint.baseEndpointInput / "aircraft-list")
      .name("get-aircraft-list-endpoint")
      .description("Retrieve aircraft list endpoint")
      .out(jsonBody[Seq[AircraftDto]].example(Seq(apiAircraftMIGExample, apiAircraftLVLExample, apiAircraftNFZExample)))
      .errorOut(statusCode)

  lazy val postAircraftEndpoint: PublicEndpoint[AircraftDto, StatusCode, AircraftDto, Any] =
    endpoint.post
      .in(CommonEndpoint.baseEndpointInput / "aircrafts")
      .name("post-aircraft-endpoint")
      .description("Create Aircraft Endpoint")
      .in(jsonBody[AircraftDto].example(apiAircraftMIGExample))
      .out(
        statusCode(StatusCode.Created)
          .and(jsonBody[AircraftDto].example(apiAircraftMIGExample))
      )
      .errorOut(statusCode)

  lazy val getRoute: Route =
    AkkaHttpServerInterpreter().toRoute(
      getAircraftEndpoint.serverLogicSuccess { _ =>
        Future.successful(apiAircraftMIGExample)
      }
    )

  lazy val getSeqRoute: Route =
    AkkaHttpServerInterpreter().toRoute(
      getAircraftSeqEndpoint.serverLogicSuccess { _ =>
        Future.successful(
          Seq(apiAircraftMIGExample.copy(id = Some(1234)), apiAircraftLVLExample.copy(id = Some(5678)))
        )
      }
    )

  lazy val postRoute: Route =
    AkkaHttpServerInterpreter().toRoute(
      postAircraftEndpoint.serverLogicSuccess { aircraft =>
        Future.successful(aircraft.copy(id = Some(1234L)))
      }
    )

  val limitQuery: EndpointInput.Query[Option[Int]] =
    query[Option[Int]]("limit")
}

object AircraftApi extends AircraftApi {}
