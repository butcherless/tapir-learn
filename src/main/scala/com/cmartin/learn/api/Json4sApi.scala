package com.cmartin.learn.api

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteConcatenation._
import com.cmartin.learn.api.ApiModel.AircraftDto
import org.json4s.JValue
import sttp.model.StatusCode
import sttp.tapir.json.json4s._
import sttp.tapir.server.akkahttp._
import sttp.tapir._
import sttp.tapir.generic.auto._
//import sttp.tapir.json.circe._
import sttp.tapir.server.akkahttp._
import io.circe.generic.auto._

import scala.concurrent.Future

trait Json4sApi {

  lazy val routes: Route =
    getRoute ~
      getJsonRoute ~
      postJsonRoute ~
      postEntityRoute

  // Json4s Codec for case class
  lazy val getAircraftEndpoint: Endpoint[Unit, StatusCode, AircraftDto, Any] =
    endpoint.get
      .name("get-json4s-endpoint")
      .description("Retrieve aircraft json4s endpoint")
      .in(CommonEndpoint.baseEndpointInput / "json4s")
      .out(jsonBody[AircraftDto].example(AircraftEndpoint.apiAircraftMIGExample))
      .errorOut(statusCode)

  lazy val getRoute: Route =
    getAircraftEndpoint.toRoute { _ =>
      Future.successful(Right(AircraftEndpoint.apiAircraftMIGExample))
    }

  // Json4s Codec for JSON - get method
  lazy val getJsonEndpoint: Endpoint[Unit, StatusCode, JValue, Any] =
    endpoint.get
      .in(CommonEndpoint.baseEndpointInput / "jvalues")
      .name("get-jvalue-endpoint")
      .description("Retrieve JValue aircraft json4s endpoint")
      .out(jsonBody[JValue].example(AircraftEndpoint.jValueAircraftExample))
      .errorOut(statusCode)

  lazy val getJsonRoute: Route =
    getJsonEndpoint.toRoute { _ =>
      Future.successful(Right(AircraftEndpoint.jValueAircraftExample))
    }

  // any JSON document, no extracting case class
  lazy val postJsonEndpoint: Endpoint[JValue, StatusCode, JValue, Any] = {
    endpoint.post
      .name("post-jvalue-endpoint")
      .description("Create JValue aircraft json4s endpoint")
      .in(CommonEndpoint.baseEndpointInput / "jvalues")
      .in(jsonBody[JValue].example(AircraftEndpoint.jValueAircraftExample))
      .out(
        statusCode(StatusCode.Created)
          .and(jsonBody[JValue].example(AircraftEndpoint.jValueAircraftExample))
      )
      .errorOut(statusCode)
  }

  lazy val postJsonRoute: Route =
    postJsonEndpoint.toRoute { _ =>
      Future.successful(Right(AircraftEndpoint.jValueAircraftExample))
    }

  lazy val postEntityEndpoint: Endpoint[AircraftDto, StatusCode, AircraftDto, Any] =
    endpoint.post
      .name("post-entity-endpoint")
      .description("Create entity aircraft json4s endpoint")
      .in(CommonEndpoint.baseEndpointInput / "j2values")
      .in(jsonBody[AircraftDto].example(AircraftEndpoint.apiAircraftMIGExample))
      .out(
        statusCode(StatusCode.Created)
          .and(jsonBody[AircraftDto].example(AircraftEndpoint.apiAircraftMIGExample))
      )
      .errorOut(statusCode)

  lazy val postEntityRoute: Route =
    postEntityEndpoint.toRoute { entity =>
      log.debug(s"postEntityRoute.request: $entity")
      Future.successful(Right(AircraftEndpoint.apiAircraftNFZExample))
    }

}

object Json4sApi extends Json4sApi
