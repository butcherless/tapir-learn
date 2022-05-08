package com.cmartin.learn.api

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteConcatenation._
import com.cmartin.learn.api.Model.AircraftDto
import com.cmartin.learn.api.Model.AircraftType
import com.github.mlangc.slf4zio.api.LoggingSupport
import org.json4s.JValue
import org.json4s._
import org.json4s.ext.EnumNameSerializer
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.json4s._
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Json4sApi extends LoggingSupport {

  implicit val serialization: Serialization = org.json4s.native.Serialization
  implicit val formats: Formats = DefaultFormats + new EnumNameSerializer(AircraftType)

  lazy val routes: Route =
    getRoute ~
      getJsonRoute ~
      postJsonRoute ~
      postEntityRoute

  // Json4s Codec for case class
  lazy val getAircraftEndpoint: PublicEndpoint[Unit, StatusCode, AircraftDto, Any] =
    endpoint.get
      .name("get-json4s-endpoint")
      .description("Retrieve aircraft json4s endpoint")
      .in(CommonEndpoint.baseEndpointInput / "json4s")
      .out(jsonBody[AircraftDto].example(AircraftEndpoint.apiAircraftMIGExample))
      .errorOut(statusCode)

  lazy val getRoute: Route =
    AkkaHttpServerInterpreter().toRoute(
      getAircraftEndpoint.serverLogicSuccess { _ =>
        Future.successful(AircraftEndpoint.apiAircraftMIGExample)
      }
    )

  // Json4s Codec for JSON - get method
  lazy val getJsonEndpoint: PublicEndpoint[Unit, StatusCode, JValue, Any] =
    endpoint.get
      .in(CommonEndpoint.baseEndpointInput / "jvalues")
      .name("get-jvalue-endpoint")
      .description("Retrieve JValue aircraft json4s endpoint")
      .out(jsonBody[JValue].example(AircraftEndpoint.jValueAircraftExample))
      .errorOut(statusCode)

  lazy val getJsonRoute: Route =
    AkkaHttpServerInterpreter().toRoute(
      getJsonEndpoint.serverLogicSuccess { _ =>
        Future.successful(AircraftEndpoint.jValueAircraftExample)
      }
    )

  // any JSON document, no extracting case class
  lazy val postJsonEndpoint: PublicEndpoint[JValue, StatusCode, JValue, Any] = {
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
    AkkaHttpServerInterpreter().toRoute(
      postJsonEndpoint.serverLogicSuccess { _ =>
        Future.successful(AircraftEndpoint.jValueAircraftExample)
      }
    )

  lazy val postEntityEndpoint: PublicEndpoint[AircraftDto, StatusCode, AircraftDto, Any] =
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
    AkkaHttpServerInterpreter().toRoute(
      postEntityEndpoint.serverLogicSuccess { entity =>
        // TODO refactor zio.Task & slf4zio: log.debug(s"postEntityRoute.request: $entity")
        Future.successful(AircraftEndpoint.apiAircraftNFZExample)
      }
    )

}

object Json4sApi extends Json4sApi
