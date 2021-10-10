package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Model.RouteDbo
import com.cmartin.aviation.repository.RouteRepository
import com.cmartin.aviation.repository.zioimpl.Tables.Routes
import com.cmartin.aviation.repository.zioimpl.common.Dbio2Zio
import slick.interop.zio.DatabaseProvider
import slick.interop.zio.syntax._
import slick.jdbc.JdbcProfile
import zio.Has
import zio.IO
import zio.ZIO
import zio.ZLayer

import Abstractions.AbstractLongRepository

class RouteRepositoryLive(db: DatabaseProvider, profile: JdbcProfile)
    extends AbstractLongRepository[RouteDbo, Routes](db, profile)
    with RouteRepository {

  import profile.api._

  override val entities = Tables.routes
  private val airports = Tables.airports

  override def findByIataOrigin(iataCode: String): IO[Throwable, Seq[RouteDbo]] = {
    val query = for {
      route <- entities
      airport <- route.origin if airport.iataCode === iataCode
    } yield route

    query.result
      .toZio
      .provide(Has(db))
  }
  override def findByIataDestination(iataCode: String): IO[Throwable, Seq[RouteDbo]] = {
    val query = for {
      route <- entities
      airport <- route.destination if airport.iataCode === iataCode
    } yield route

    query.result
      .toZio
      .provide(Has(db))
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
      .toZio
      .provide(Has(db))
  }

  override def deleteByOriginAndDestination(iataOrigin: String, iataDestination: String): IO[Throwable, Int] = {
    val airportsQuery = for {
      origin <- airports if origin.iataCode === iataOrigin
      destination <- airports if destination.iataCode === iataDestination
    } yield (origin, destination)

    val program = for {
      (origin, destination) <- ZIO.fromDBIO(airportsQuery.result.head)
      count <- ZIO.fromDBIO(
        entities.filter(route => route.originId === origin.id && route.destinationId === destination.id).delete
      )
    } yield count

    program
      .provide(Has(db))
  }

}

object RouteRepositoryLive {

  val layer: ZLayer[Has[DatabaseProvider], Throwable, Has[RouteRepository]] =
    ZLayer.fromServiceM { provider =>
      provider.profile.map { profile =>
        new RouteRepositoryLive(provider, profile)
      }
    }

}
