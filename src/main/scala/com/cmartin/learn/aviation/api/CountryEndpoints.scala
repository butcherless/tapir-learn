package com.cmartin.learn.aviation.api

import akka.http.scaladsl.model.headers.`Content-Location`
import io.circe.Json
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._

import Model._
import BaseEndpoint._

trait CountryEndpoints {

  lazy val getByCodeEndpoint: Endpoint[String, OutputError, CountryView, Any] =
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

  lazy val postEndpoint
      : Endpoint[CountryView, OutputError, (String, CountryView), Any] =
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

  lazy val deleteEndpoint: Endpoint[String, OutputError, Unit, Any] =
    baseEndpoint.delete
      .name("country-delete-endpoint")
      .description("Deletes a Country by its code")
      .in(countryPath)
      .in(codePath)
      .out(statusCode(StatusCode.NoContent))
      .errorOut(
        oneOf[OutputError](
          badRequestMapping,
          internalErrorMapping,
          defaultMapping
        )
      )

  lazy val countriesResource = "countries"
  lazy val countryPath = baseApiResource / countriesResource
  lazy val codePath = path[String]("code")
}

object CountryEndpoints extends CountryEndpoints {
  val countryViewExample = CountryView("es", "Spain")
}
