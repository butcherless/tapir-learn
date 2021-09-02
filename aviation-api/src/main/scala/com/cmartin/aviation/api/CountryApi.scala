package com.cmartin.aviation.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.cmartin.aviation.api.validator.CountryValidator
import com.cmartin.aviation.api.validator.CountryValidator._
import com.cmartin.aviation.domain.Model._
import com.cmartin.aviation.port.CountryService
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
import zio.logging._

import Common._
import Model._
import BaseEndpoint._

class CountryApi(countryService: CountryService) {
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

  private def doGetLogic(request: String): ApiResponse[CountryView] = {
    for {
      _ <- log.debug(s"doGetLogic - request: $request")
      criteria <- CountryValidator.validateCode(request).toIO
      country <- countryService.findByCode(criteria)
    } yield country.toView
  }

  private def doPostLogic(request: CountryView): ApiResponse[(String, CountryView)] = {
    for {
      _ <- log.debug(s"doPostLogic - request: $request")
      country <- CountryValidator.validatePostRequest(request).toIO
      created <- countryService.create(country)
    } yield (
      buildContentLocation(CountryEndpoints.countriesResource, created.code),
      created.toView
    )
  }

  private def doPutLogic(request: CountryView): ApiResponse[CountryView] = {
    for {
      _ <- log.debug(s"doPutLogic - request: $request")
      country <- CountryValidator.validatePutRequest(request).toIO
      updated <- countryService.update(country)
    } yield updated.toView
  }

  private def doDeleteLogic(request: String): ApiResponse[Unit] = {
    for {
      _ <- log.debug(s"doDeleteLogic - request: $request")
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
