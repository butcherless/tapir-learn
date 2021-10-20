package com.cmartin.aviation.api

import akka.http.scaladsl.server.Route
import com.cmartin.aviation.api.Common.Api2Response
import com.cmartin.aviation.api.Model.CountryView
import com.cmartin.aviation.domain.Model.CountryCode
import com.cmartin.aviation.port.CountryPersister
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
import zio.logging._
import zio._

class CountryPocApi(
    logging: Logging,
    countryPersister: CountryPersister
) {
  lazy val routes: Route =
    getRoute

  lazy val getRoute: Route =
    AkkaHttpServerInterpreter()
      .toRoute(CountryEndpoints.getByCodeEndpoint) { request =>
        Common.run2(
          doGetLogic(request)
        )
      }

  private def doGetLogic(request: String): Api2Response[CountryView] = {
    val program = for {
      _ <- log.debug(s"doGetLogic - request: $request")
      _ <- countryPersister.findByCode(CountryCode(request))
    } yield CountryView(CountryCode("es"), "spain")
    program
      .provide(logging)
  }
}

object CountryPocApi {
  def apply(logging: Logging, countryPersister: CountryPersister): CountryPocApi =
    new CountryPocApi(logging, countryPersister)

  val layer: URLayer[Has[Logging] with Has[CountryPersister], Has[CountryPocApi]] =
    (CountryPocApi(_, _)).toLayer
}

object SimulatorSpec {

  val c = CountryPocApi.layer

}
