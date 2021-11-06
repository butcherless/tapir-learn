package com.cmartin.aviation.api

import akka.http.scaladsl.model.headers.`Content-Location`
import com.cmartin.aviation.domain.Model.CountryCode
import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._

import Model._
import BaseEndpoint._

trait CountryEndpoints {
  import CountryEndpoints.Implicits._

  lazy val getByCodeEndpoint: PublicEndpoint[String, OutputError, CountryView, Any] =
    baseEndpoint.get
      .name("country-get-by-code-endpoint")
      .description("Retrieves a Country by its code")
      .in(countryPath)
      .in(codePath)
      .out(jsonBody[CountryView].example(CountryEndpoints.countryViewExample))
      .errorOut(
        oneOf[OutputError](
          badRequestMapping,
          notFoundMapping,
          internalErrorMapping,
          defaultMapping
        )
      )

  lazy val postEndpoint: PublicEndpoint[CountryView, OutputError, (String, CountryView), Any] =
    baseEndpoint.post
      .name("country-post-endpoint")
      .description("Creates a Country")
      .in(countryPath)
      .in(jsonBody[CountryView].example(CountryEndpoints.countryViewExample))
      .out(
        statusCode(StatusCode.Created)
          .and(header[String](`Content-Location`.name))
          .and(jsonBody[CountryView])
      )
      .errorOut(
        oneOf[OutputError](
          badRequestMapping,
          internalErrorMapping,
          defaultMapping
        )
      )

  lazy val putEndpoint: PublicEndpoint[CountryView, OutputError, CountryView, Any] =
    baseEndpoint.put
      .name("country-put-endpoint")
      .description("Updates a Country")
      .in(countryPath)
      .in(jsonBody[CountryView].example(CountryEndpoints.countryViewExample))
      .out(
        statusCode(StatusCode.Ok)
          .and(jsonBody[CountryView].example(CountryEndpoints.countryViewExample))
      )
      .errorOut(
        oneOf[OutputError](
          badRequestMapping,
          internalErrorMapping,
          defaultMapping
        )
      )

  lazy val deleteEndpoint: PublicEndpoint[String, OutputError, Unit, Any] =
    baseEndpoint.delete
      .name("country-delete-endpoint")
      .description("Deletes a Country by its code")
      .in(countryPath)
      .in(codePath)
      .out(statusCode(StatusCode.NoContent))
      .errorOut(
        oneOf[OutputError](
          badRequestMapping,
          notFoundMapping,
          internalErrorMapping,
          defaultMapping
        )
      )

  lazy val countriesResource = "countries"
  lazy val countryPath: EndpointInput[Unit] = countriesResource
  lazy val codePath = path[String]("code")
}

object CountryEndpoints extends CountryEndpoints {
  val countryViewExample = CountryView(CountryCode("es"), "Spain")

  object Implicits {
    implicit val encodeCountryCode: Encoder[CountryCode] =
      Encoder.encodeString.contramap[CountryCode](CountryCode.unwrap)
    implicit val decodeCountryCode: Decoder[CountryCode] =
      Decoder.decodeString.map(CountryCode(_))
    implicit val countryCodeSchema: Schema[CountryCode] =
      Schema(SchemaType.SString())
  }
}
