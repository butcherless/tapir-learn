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

import Common._
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

  lazy val putRoute: Route =
    AkkaHttpServerInterpreter()
      .toRoute(CountryEndpoints.putEndpoint) { request =>
        run(
          doPutLogic(request)
        )
      }

  lazy val deleteRoute: Route =
    AkkaHttpServerInterpreter()
      .toRoute(CountryEndpoints.deleteEndpoint) { request =>
        run(
          doDeleteLogic(request)
        )
      }

  private def doGetLogic(request: String): IO[ProgramError, CountryView] = {
    for {
      _ <- logger.debugIO(s"doGetLogic - request: $request")
      criteria <- CountryValidator.validateCode(request).toIO
      country <- countryService.findByCode(criteria)
    } yield country.toView
  }

  private def doPostLogic(request: CountryView): IO[ProgramError, (String, CountryView)] = {
    for {
      _ <- logger.debugIO(s"doPostLogic - request: $request")
      country <- CountryValidator.validatePostRequest(request).toIO
      country <- countryService.create(country)
    } yield (
      buildContentLocation(CountryEndpoints.countriesResource, country.code),
      country.toView
    )
  }

  private def doPutLogic(request: CountryView): IO[ProgramError, CountryView] = {
    for {
      _ <- logger.debugIO(s"doPostLogic - request: $request")
    } yield CountryEndpoints.countryViewExample
  }

  private def doDeleteLogic(request: String): IO[ProgramError, Unit] = {
    for {
      _ <- logger.debugIO(s"doDeleteLogic - request: $request")
      criteria <- CountryValidator.validateDeleteRequest(request).toIO
      _ <- countryService.deleteByCode(criteria)
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
