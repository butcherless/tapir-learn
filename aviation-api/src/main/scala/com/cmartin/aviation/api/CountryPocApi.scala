package com.cmartin.aviation.api

import akka.http.scaladsl.server.Route
import com.cmartin.aviation.api.Common.Api2Response
import com.cmartin.aviation.api.Model.CountryView
import com.cmartin.aviation.domain
import com.cmartin.aviation.domain.Model.CountryCode
import com.cmartin.aviation.port.CountryPersister
import sttp.tapir._
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
import zio._
import zio.logging._

class CountryPocApi(countryPersister: CountryPersister) {
  lazy val routes: Route =
    getRoute

  lazy val getRoute: Route =
    AkkaHttpServerInterpreter().toRoute(
      CountryEndpoints.getByCodeEndpoint.serverLogic { request =>
        Common.run2(doGetLogic(request))
      }
    )

  def doGetLogic(request: String): Api2Response[CountryView] = {
    for {
      _ <- ZIO.debug(s"doGetLogic - request: $request")
      _ <- countryPersister.findByCode(CountryCode(request))
    } yield CountryView(CountryCode("es"), "spain")
  }
}

object CountryPocApi {
  def apply(countryPersister: CountryPersister): CountryPocApi =
    new CountryPocApi(countryPersister)

  def doGetLogic(request: String): ZIO[CountryPocApi, domain.Model.ProgramError, CountryView] =
    ZIO.serviceWithZIO[CountryPocApi](_.doGetLogic("request"))

  val layer: URLayer[CountryPersister, CountryPocApi] =
    ZLayer.fromFunction(p => CountryPocApi(p))
}

object SimulatorSpec {

  val c = CountryPocApi.layer

  val env = CountryPocApi.layer

  val program = for {
    s1 <- CountryPocApi.doGetLogic("request")
  } yield s1

  // TODO create api test to verify dependencies
  // val res = runtime.unsafeRun(program.provideLayer(env))
}
