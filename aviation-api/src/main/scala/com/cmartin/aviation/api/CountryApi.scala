package com.cmartin.aviation.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.cmartin.aviation.api.validator.CountryValidator
import com.cmartin.aviation.api.validator.CountryValidator._
import com.cmartin.aviation.domain.CountryService
import com.cmartin.aviation.domain.Model._
import com.github.mlangc.slf4zio.api._
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
import zio.IO

import Common.run
import Model._
import BaseEndpoint._

class CountryApi(countryService: CountryService) extends LoggingSupport {
  import CountryApi._

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
      criteria <- CountryValidator.validateGetRequest(request).toIO
      country <- countryService.findByCode(criteria)
    } yield country.toView
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

object CountryApi {
  def apply(countryService: CountryService): CountryApi =
    new CountryApi(countryService)

  implicit class ModelToView(country: Country) {
    def toView: CountryView =
      CountryView(country.code, country.name)
  }
}
