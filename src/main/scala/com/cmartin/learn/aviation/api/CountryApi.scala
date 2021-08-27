package com.cmartin.learn.aviation.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.cmartin.learn.aviation.api.validator.CountryValidator
import com.cmartin.learn.aviation.api.validator.CountryValidator._
import com.github.mlangc.slf4zio.api._
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
import zio.IO

import Common.run
import Model._
import BaseEndpoint._

class CountryApi extends LoggingSupport {

  lazy val routes: Route =
    getRoute ~
      postRoute ~
      deleteRoute

  lazy val getRoute: Route =
    AkkaHttpServerInterpreter()
      .toRoute(CountryEndpoints.getByCodeEndpoint) { request =>
        run(
          doGetLogic(request)
        )
      }

  lazy val postRoute: Route =
    AkkaHttpServerInterpreter()
      .toRoute(CountryEndpoints.postEndpoint) { request =>
        run(
          doPostLogic(request)
        )
      }

  lazy val deleteRoute: Route =
    AkkaHttpServerInterpreter()
      .toRoute(CountryEndpoints.deleteEndpoint) { request =>
        run(
          doDeleteLogic(request)
        )
      }

  private def doGetLogic(request: String): IO[DomainError, CountryView] = {
    for {
      _ <- logger.debugIO(s"doGetLogic - request: $request")
      // TODO validate / smart constructor
      criteria <- CountryValidator.validateGetRequest(request).toIO
      // TODO call service operation
    } yield CountryEndpoints.countryViewExample
  }

  private def doPostLogic(request: CountryView): IO[DomainError, (String, CountryView)] = {
    for {
      _ <- logger.debugIO(s"doPostLogic - request: $request")
      // TODO validate / smart constructor
      // TODO call service operation
    } yield (
      buildContentLocation(CountryEndpoints.countriesResource, "dummy-id"),
      CountryEndpoints.countryViewExample
    )
  }

  private def doDeleteLogic(request: String): IO[DomainError, Unit] = {
    for {
      _ <- logger.debugIO(s"doDeleteLogic - request: $request")
      // TODO validate / smart constructor
      // TODO call service operation
    } yield ()
  }
}
