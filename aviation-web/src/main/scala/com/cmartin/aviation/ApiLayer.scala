package com.cmartin.aviation

import sttp.apispec.openapi.Info
import sttp.model.{HeaderNames, StatusCode}
import sttp.tapir.{EndpointInput, PublicEndpoint, Schema, SchemaType}
import sttp.tapir.generic.auto._
import sttp.tapir.json.zio._
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir._
import zio._
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}
import zio.prelude.Subtype
import com.cmartin.aviation.domain.Model
import ServiceLayer.CountryService

object ApiLayer {

  object ApiModel {

    /* ERROR MODEL */
    sealed trait ErrorInfo
    case class NotFound(text: String)     extends ErrorInfo
    case class Conflict(text: String)     extends ErrorInfo
    case class Unauthorized(text: String) extends ErrorInfo
    case class Unknown(text: String)      extends ErrorInfo
    case object NoContent                 extends ErrorInfo

    object CountryCode extends Subtype[String] {
      implicit val encoder: JsonEncoder[CountryCode] =
        JsonEncoder[String].contramap(CountryCode.unwrap)
      implicit val decoder: JsonDecoder[CountryCode] =
        JsonDecoder[String].map(code => CountryCode(code))
      implicit val schema: Schema[CountryCode]       =
        Schema(SchemaType.SString())

    }
    type CountryCode = CountryCode.Type

    /* TODO add CountryCode Subtype
       TODO add View Schema
         https://tapir.softwaremill.com/en/latest/endpoint/schemas.html?highlight=schema#manually-providing-schemas
     */
    case class CountryView(
        code: CountryCode,
        name: String
    )
    case class AirportView(
        name: String,
        iataCode: String,
        icaoCode: String,
        airportCode: String
    )

    object CountryView {
      implicit val decoder: JsonDecoder[CountryView] = DeriveJsonDecoder.gen[CountryView]
      implicit val encoder: JsonEncoder[CountryView] = DeriveJsonEncoder.gen[CountryView]
    }

    object AirportView {
      implicit val decoder: JsonDecoder[AirportView] = DeriveJsonDecoder.gen[AirportView]
      implicit val encoder: JsonEncoder[AirportView] = DeriveJsonEncoder.gen[AirportView]
    }

    object NotFound {
      implicit val decoder: JsonDecoder[NotFound] = DeriveJsonDecoder.gen[NotFound]
      implicit val encoder: JsonEncoder[NotFound] = DeriveJsonEncoder.gen[NotFound]
    }

    object Conflict {
      implicit val decoder: JsonDecoder[Conflict] = DeriveJsonDecoder.gen[Conflict]
      implicit val encoder: JsonEncoder[Conflict] = DeriveJsonEncoder.gen[Conflict]
    }

    object Unauthorized {
      implicit val decoder: JsonDecoder[Unauthorized] = DeriveJsonDecoder.gen[Unauthorized]
      implicit val encoder: JsonEncoder[Unauthorized] = DeriveJsonEncoder.gen[Unauthorized]
    }

    object Unknown {
      implicit val decoder: JsonDecoder[Unknown] = DeriveJsonDecoder.gen[Unknown]
      implicit val encoder: JsonEncoder[Unknown] = DeriveJsonEncoder.gen[Unknown]
    }

  }

  /**/
  object Endpoints {
    import ApiModel.{Unauthorized, Unknown}

    val commonMappings = List(
      oneOfVariant(statusCode(StatusCode.Unauthorized).and(jsonBody[Unauthorized].description("unauthorized"))),
      oneOfDefaultVariant(jsonBody[Unknown].description("service error"))
    )
  }

  object CountryEndpoints {
    import ApiModel._
    import Endpoints.commonMappings
    import ServiceLayer.Domain.{CountryNotFound, DuplicateEntityError, ServiceError}

    lazy val countriesResource                = "countries"
    lazy val countryPath: EndpointInput[Unit] = countriesResource
    lazy val codePath                         = path[String]("code").description("Country code")

    val getEndpoint: PublicEndpoint[String, ErrorInfo, CountryView, Any] =
      endpoint.get
        .name("get-by-code-endpoint")
        .description("Retrieves a Country by its code")
        .in(countryPath)
        .in(codePath)
        .out(jsonBody[CountryView].example(countryViewExample))
        .errorOut(
          oneOf[ErrorInfo](
            oneOfVariant(statusCode(StatusCode.NotFound).and(jsonBody[NotFound].description("resource not found"))),
            commonMappings: _*
          )
        )

    val getAllEndpoint: PublicEndpoint[Unit, ErrorInfo, Seq[CountryView], Any] =
      endpoint.get
        .name("get-all-endpoint")
        .description("Retrieves a finite Country sequence")
        .in(countryPath)
        .out(jsonBody[Seq[CountryView]].example(countryViewSeqExample))
        .errorOut(
          oneOf[ErrorInfo](
            oneOfVariant(statusCode(StatusCode.NotFound).and(jsonBody[NotFound].description("not found"))),
            commonMappings: _*
          )
        )

    lazy val postEndpoint: PublicEndpoint[CountryView, ErrorInfo, String, Any] =
      endpoint.post
        .name("post-endpoint")
        .description("Creates a Country")
        .in(countryPath)
        .in(jsonBody[CountryView].example(countryViewExample))
        .out(
          statusCode(StatusCode.Created)
            .and(header[String](HeaderNames.ContentLocation))
          // .and(jsonBody[CountryView])
        ).errorOut(
          oneOf[ErrorInfo](
            oneOfVariant(statusCode(StatusCode.NoContent).and(emptyOutputAs(NoContent))),
            (oneOfVariant(
              statusCode(StatusCode.Conflict).and(jsonBody[Conflict].description("duplicated"))
            ) +: commonMappings): _*
          )
        )

    lazy val putEndpoint: PublicEndpoint[CountryView, ErrorInfo, CountryView, Any] =
      endpoint.put
        .name("put-endpoint")
        .description("Updates a Country")
        .in(countryPath)
        .in(jsonBody[CountryView].example(countryViewExample))
        .out(
          statusCode(StatusCode.Ok)
            .and(jsonBody[CountryView].example(countryViewExample))
        )
        .errorOut(
          oneOf[ErrorInfo](
            oneOfVariant(statusCode(StatusCode.NoContent).and(emptyOutputAs(NoContent))),
            commonMappings: _*
          )
        )

    lazy val deleteEndpoint: PublicEndpoint[String, ErrorInfo, Unit, Any] =
      endpoint.delete
        .name("delete-endpoint")
        .description("Deletes a Country by its code")
        .in(countryPath)
        .in(codePath)
        .out(statusCode(StatusCode.NoContent))
        .errorOut(
          oneOf[ErrorInfo](
            oneOfVariant(statusCode(StatusCode.NotFound).and(jsonBody[NotFound].description("not found"))),
            commonMappings: _*
          )
        )

    lazy val serverEndpoints = List(
      getEndpoint.zServerLogic(getByCodeLogic),
      postEndpoint.zServerLogic(postLogic)
    )

    lazy val countryViewExample    = CountryView(CountryCode("es"), "Spain")
    lazy val ptCountryViewExample  = CountryView(CountryCode("pt"), "Portugal")
    lazy val countryViewSeqExample = Seq(countryViewExample, ptCountryViewExample)

    // helper functions
    private def getByCodeLogic(code: String): IO[ErrorInfo, CountryView] =
      (for {
        _           <- ZIO.logInfo(s"getCountryByCode: $code)")
        country     <- CountryService.searchByCode(code)
        countryView <- ZIO.succeed(CountryView(CountryCode(country.code), country.name))
      } yield countryView)
        .mapError(manageError) // TODO implicit conversion

    private def postLogic(view: CountryView): IO[ErrorInfo, String] =
      (for {
        _      <- ZIO.logInfo(s"create: $view)")
        country = Model.Country(Model.CountryCode(view.code), view.name)
        _      <- CountryService.create(country)
      } yield s"/todo/url/${country.code}")
        .mapError(manageError) // TODO implicit conversion

    // ServiceError => ApiError
    private def manageError(serviceError: ServiceError): ErrorInfo =
      serviceError match {
        case CountryNotFound(code)        => NotFound(code)
        case DuplicateEntityError(entity) => Conflict(entity)
        case _                            => Unknown("Service Error")
      }
  }

  object AirportEndpoints {
    import ApiModel._
    import Endpoints.commonMappings

    lazy val airportsResource = "airports"
    lazy val airportPath      = airportsResource
    lazy val iataCodePath     = path[String]("iataCode")

    lazy val getByIataCodeEndpoint: PublicEndpoint[String, ErrorInfo, AirportView, Any] =
      endpoint.get
        .name("get-by-iata-code-endpoint")
        .description("Retrieves an Airport by its iata code")
        .in(airportPath)
        .in(iataCodePath)
        .out(jsonBody[AirportView].example(airportViewExample))
        .errorOut(
          oneOf[ErrorInfo](
            oneOfVariant(statusCode(StatusCode.NotFound).and(jsonBody[NotFound].description("not found"))),
            commonMappings: _*
          )
        )

    lazy val postEndpoint: PublicEndpoint[AirportView, ErrorInfo, (String, AirportView), Any] =
      endpoint.post
        .name("post-endpoint")
        .description("Creates an Airport")
        .in(airportPath)
        .in(jsonBody[AirportView].example(airportViewExample))
        .out(
          statusCode(StatusCode.Created)
            .and(header[String](HeaderNames.ContentLocation))
            .and(jsonBody[AirportView])
        )
        .errorOut(
          oneOf[ErrorInfo](
            oneOfVariant(statusCode(StatusCode.NoContent).and(emptyOutputAs(NoContent))),
            commonMappings: _*
          )
        )

    lazy val countryPutEndpoint: PublicEndpoint[AirportView, ErrorInfo, AirportView, Any] =
      endpoint.put
        .name("put-endpoint")
        .description("Updates an Airport")
        .in(airportPath)
        .in(jsonBody[AirportView].example(airportViewExample))
        .out(
          statusCode(StatusCode.Ok)
            .and(jsonBody[AirportView].example(airportViewExample))
        )
        .errorOut(
          oneOf[ErrorInfo](
            oneOfVariant(statusCode(StatusCode.NoContent).and(emptyOutputAs(NoContent))),
            commonMappings: _*
          )
        )

    lazy val deleteEndpoint: PublicEndpoint[String, ErrorInfo, Unit, Any] =
      endpoint.delete
        .name("delete-endpoint")
        .description("Deletes an Airport by its iata code")
        .in(airportPath)
        .in(iataCodePath)
        .out(statusCode(StatusCode.NoContent))
        .errorOut(
          oneOf[ErrorInfo](
            oneOfVariant(statusCode(StatusCode.NotFound).and(jsonBody[NotFound].description("not found"))),
            commonMappings: _*
          )
        )

    lazy val airportViewExample = AirportView("Madrid Barajas", "MAD", "LEMD", "es")
  }

  object SwaggerDocs {

    val info: Info = Info("Martin Air API", "1.0")

    val swaggerEndpoints: List[ZServerEndpoint[Any, Any]] =
      SwaggerInterpreter().fromEndpoints[Task](
        List(
          CountryEndpoints.getEndpoint,
          CountryEndpoints.getAllEndpoint,
          CountryEndpoints.postEndpoint,
          CountryEndpoints.putEndpoint,
          CountryEndpoints.deleteEndpoint,
          AirportEndpoints.getByIataCodeEndpoint,
          AirportEndpoints.postEndpoint,
          AirportEndpoints.deleteEndpoint
        ),
        info
      )
  }

}
