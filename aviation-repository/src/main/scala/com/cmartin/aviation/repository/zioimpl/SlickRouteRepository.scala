package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Model.RouteDbo
import com.cmartin.aviation.repository.RouteRepository
import com.cmartin.aviation.repository.zioimpl.CommonAbstractions.Repository.AbstractLongRepository
import com.cmartin.aviation.repository.zioimpl.Tables.Routes
import com.cmartin.aviation.repository.zioimpl.common.SlickToZioSyntax.fromDBIO
import slick.jdbc.JdbcBackend
import slick.jdbc.JdbcProfile
import zio.IO
import zio.RLayer
import zio.ZLayer

case class SlickRouteRepository(db: JdbcBackend#DatabaseDef)
    extends AbstractLongRepository[RouteDbo, Routes](db)
    with JdbcProfile
    with RouteRepository {

  import api._
  import common.Implicits.Dbio2Zio

  override val entities = Tables.routes
  private val airports = Tables.airports

  override def findByIataOrigin(iataCode: String): IO[Throwable, Seq[RouteDbo]] = {
    val query = for {
      route <- entities
      airport <- route.origin if airport.iataCode === iataCode
    } yield route

    query.result
      .toZio()
      .provideService(db)
  }
  override def findByIataDestination(iataCode: String): IO[Throwable, Seq[RouteDbo]] = {
    val query = for {
      route <- entities
      airport <- route.destination if airport.iataCode === iataCode
    } yield route

    query.result
      .toZio()
      .provideService(db)
  }

  override def findByOriginAndDestination(
      iataOrigin: String,
      iataDestination: String
  ): IO[Throwable, Option[RouteDbo]] = {
    val query = for {
      route <- entities
      origin <- route.origin if origin.iataCode === iataOrigin
      destination <- route.destination if destination.iataCode === iataDestination
    } yield route

    query.result.headOption
      .toZio()
      .provideService(db)
  }

  override def deleteByOriginAndDestination(iataOrigin: String, iataDestination: String): IO[Throwable, Int] = {
    val airportsQuery = for {
      origin <- airports if origin.iataCode === iataOrigin
      destination <- airports if destination.iataCode === iataDestination
    } yield (origin, destination)

    val program = for {
      coords <- fromDBIO(airportsQuery.result.head)
      (origin, destination) = coords
      count <- fromDBIO(
        entities.filter(route => route.originId === origin.id && route.destinationId === destination.id).delete
      )
    } yield count

    program
      .provideService(db)
  }

}

object SlickRouteRepository {
  val layer: RLayer[JdbcBackend#DatabaseDef, RouteRepository] =
    ZLayer.fromFunction(SlickRouteRepository(_))
}
