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

trait AirportEndpoints {

  lazy val getByIataCodeEndpoint
      : Endpoint[String, OutputError, AirportView, Any] =
    baseEndpoint.get
      .name("airport-get-by-iata-code-endpoint")
      .description("Retrieves an Airport by its iata code")
      .in(airportPath)
      .in(iataCodePath)
      .out(jsonBody[AirportView].example(AirportEndpoints.airportViewExample))
      .errorOut(
        oneOf[OutputError](
          badRequestMapping,
          notFoundMapping,
          internalErrorMapping,
          defaultMapping
        )
      )

  lazy val postEndpoint
      : Endpoint[AirportView, OutputError, (String, AirportView), Any] =
    baseEndpoint.post
      .name("airport-post-endpoint")
      .description("Creates an Airport")
      .in(airportPath)
      .in(jsonBody[AirportView].example(AirportEndpoints.airportViewExample))
      .out(
        statusCode(StatusCode.Created)
          .and(header[String](`Content-Location`.name))
          .and(jsonBody[AirportView])
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
      .name("airport-delete-endpoint")
      .description("Deletes an Airport by its iata code")
      .in(airportPath)
      .in(iataCodePath)
      .out(statusCode(StatusCode.NoContent))
      .errorOut(
        oneOf[OutputError](
          badRequestMapping,
          internalErrorMapping,
          defaultMapping
        )
      )

  lazy val airportsResource = "countries"
  lazy val airportPath = baseApiResource / airportsResource
  lazy val iataCodePath = path[String]("iataCode")
}

object AirportEndpoints extends AirportEndpoints {
  val airportViewExample = AirportView("Madrid Barajas", "MAD", "LEMD", "es")
}
