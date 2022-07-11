package com.cmartin.aviation.api.configuration

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteConcatenation._
import akka.http.scaladsl.server.directives.DebuggingDirectives
import com.cmartin.aviation.Commons
import com.cmartin.aviation.api.{ActuatorApi, CountryApi, SwaggerApi}
import com.cmartin.aviation.domain.Model
import com.cmartin.aviation.port.CountryService

trait ApiConfiguration {
  lazy val serverAddress: String = "localhost"
  lazy val serverPort: Int       = 8080

  // TODO dummy service
  val countryService: CountryService = new CountryService {

    override def create(country: Model.Country): Commons.ServiceResponse[Model.Country] = ???

    override def findByCode(code: Model.CountryCode): Commons.ServiceResponse[Model.Country] = ???

    override def update(country: Model.Country): Commons.ServiceResponse[Model.Country] = ???

    override def deleteByCode(code: Model.CountryCode): Commons.ServiceResponse[Int] = ???

  }

  val countryApi = CountryApi(countryService)

  lazy val routes: Route = DebuggingDirectives.logRequestResult("route-logger") {
    ActuatorApi.route ~
      countryApi.routes ~
      SwaggerApi.route
  }
}
