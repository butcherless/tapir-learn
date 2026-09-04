package com.cmartin.learn.api

import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.server.RouteConcatenation._
import com.cmartin.learn.api.Model.AircraftDto
import com.cmartin.learn.api.Model.AircraftType
import org.json4s.JValue
import org.json4s._
import org.json4s.ext.EnumNameSerializer
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.json4s._
import sttp.tapir.server.pekkohttp.PekkoHttpServerInterpreter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/*
   json4s-native's default (de)serialization inspects a case class' constructor via
   `org.json4s.reflect.ScalaSigReader`, which reads the Scala 2 pickled `ScalaSig` class-file
   attribute. Scala 3 doesn't emit that attribute (it uses TASTy instead), so the automatic path
   crashes at runtime for any case class with no matching custom serializer. A `CustomSerializer`
   is consulted before that reflective fallback, so registering one for `AircraftDto` sidesteps
   the broken path entirely while keeping the same wire format.
 */
class AircraftDtoSerializer(implicit aircraftTypeFormats: Formats)
    extends CustomSerializer[AircraftDto](_ =>
      (
        { case JObject(fields) =>
          val byName = fields.toMap
          AircraftDto(
            registration = byName("registration").extract[String],
            age = byName("age").extract[Int],
            model = AircraftType.withName(byName("model").extract[String]),
            id = byName.get("id").flatMap(_.extractOpt[Long])
          )
        },
        { case a: AircraftDto =>
          JObject(
            "registration" -> JString(a.registration),
            "age"          -> JInt(a.age),
            "model"        -> JString(a.model.toString),
            "id"           -> a.id.map(JInt(_)).getOrElse(JNull)
          )
        }
      )
    )

trait Json4sApi {

  implicit val serialization: Serialization = org.json4s.native.Serialization
  implicit val formats: Formats             =
    DefaultFormats + new EnumNameSerializer(AircraftType) + new AircraftDtoSerializer()(DefaultFormats)

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
    PekkoHttpServerInterpreter().toRoute(
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
    PekkoHttpServerInterpreter().toRoute(
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
    PekkoHttpServerInterpreter().toRoute(
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
    PekkoHttpServerInterpreter().toRoute(
      postEntityEndpoint.serverLogicSuccess { entity =>
        // TODO refactor zio.Task & slf4zio: log.debug(s"postEntityRoute.request: $entity")
        Future.successful(AircraftEndpoint.apiAircraftNFZExample)
      }
    )

}

object Json4sApi extends Json4sApi
