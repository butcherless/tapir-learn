package com.cmartin.aviation.api

import akka.http.scaladsl.model.headers.`Content-Location`
import com.cmartin.aviation.api.BaseEndpoint._
import com.cmartin.aviation.api.Model._
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._

import java.time.LocalDate

trait AirlineEndpoints {

  lazy val getByIataCodeEndpoint: PublicEndpoint[String, OutputError, AirlineView, Any] =
    baseEndpoint.get
      .name("airline-get-by-iata-code-endpoint")
      .description("Retrieves an Airline by its IATA code")
      .in(airlinePath)
      .in(codePath)
      .out(jsonBody[AirlineView].example(AirlineEndpoints.airlineViewExample))
      .errorOut(
        oneOf[OutputError](
          badRequestMapping,
          notFoundMapping,
          internalErrorMapping,
          defaultMapping
        )
      )

  lazy val postEndpoint: PublicEndpoint[AirlineView, OutputError, (String, AirlineView), Any] =
    baseEndpoint.post
      .name("airline-post-endpoint")
      .description("Creates an Airline")
      .in(airlinePath)
      .in(jsonBody[AirlineView].example(AirlineEndpoints.airlineViewExample))
      .out(
        statusCode(StatusCode.Created)
          .and(header[String](`Content-Location`.name))
          .and(jsonBody[AirlineView])
      )
      .errorOut(
        oneOf[OutputError](
          badRequestMapping,
          conflictMapping,
          internalErrorMapping,
          defaultMapping
        )
      )

  lazy val deleteEndpoint: PublicEndpoint[String, OutputError, Unit, Any] =
    baseEndpoint.delete
      .name("airline-delete-endpoint")
      .description("Deletes an Airline by its IATA code")
      .in(airlinePath)
      .in(codePath)
      .out(statusCode(StatusCode.NoContent))
      .errorOut(
        oneOf[OutputError](
          badRequestMapping,
          internalErrorMapping,
          defaultMapping
        )
      )

  lazy val airlinesResource = "airlines"
  lazy val airlinePath      = baseApiResource / airlinesResource
  lazy val codePath         = path[String]("code")
}

object AirlineEndpoints extends AirlineEndpoints {
  val airlineViewExample: AirlineView =
    AirlineView(
      "Iberia",
      "IB",
      LocalDate.of(1927, 6, 28),
      "es"
    ) // TODO date
}
