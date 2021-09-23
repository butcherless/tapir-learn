package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Model.RouteDbo
import com.cmartin.aviation.repository.zioimpl.Tables.Routes
import com.cmartin.aviation.repository.zioimpl.common.Dbio2Zio
import slick.interop.zio.DatabaseProvider
import slick.jdbc.JdbcProfile
import zio.Has
import zio.IO
import zio.ZIO
import zio.ZLayer
import slick.dbio.DBIO
import slick.interop.zio.syntax._

import Abstractions.AbstractLongRepository

object SlickRouteRepository {

  class RouteRepositoryImpl(db: DatabaseProvider, profile: JdbcProfile)
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

      (query.result, db).toZio
    }
    override def findByIataDestination(iataCode: String): IO[Throwable, Seq[RouteDbo]] = {
      val query = for {
        route <- entities
        airport <- route.destination if airport.iataCode === iataCode
      } yield route

      (query.result, db).toZio

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

      program.provide(Has(db))
    }

  }

  val live: ZLayer[Has[DatabaseProvider], Throwable, Has[RouteRepository]] =
    ZLayer.fromServiceM { provider =>
      provider.profile.map { profile =>
        new RouteRepositoryImpl(provider, profile)
      }
    }

  def insert(dbo: RouteDbo): ZIO[Has[RouteRepository], Throwable, Long] =
    ZIO.accessM[Has[RouteRepository]](_.get.insert(dbo))

  def insert(seq: Seq[RouteDbo]): ZIO[Has[RouteRepository], Throwable, Seq[Long]] =
    ZIO.accessM[Has[RouteRepository]](_.get.insert(seq))

  def update(dbo: RouteDbo): ZIO[Has[RouteRepository], Throwable, Int] =
    ZIO.accessM[Has[RouteRepository]](_.get.update(dbo))

  def deleteByOriginAndDestination(
      iataOrigin: String,
      iataDestination: String
  ): ZIO[Has[RouteRepository], Throwable, Int] =
    ZIO.accessM[Has[RouteRepository]](_.get.deleteByOriginAndDestination(iataOrigin, iataDestination))

  def findByIataOrigin(iataCode: String): ZIO[Has[RouteRepository], Throwable, Seq[RouteDbo]] =
    ZIO.accessM[Has[RouteRepository]](_.get.findByIataOrigin(iataCode))

  def findByIataDestination(iataCode: String): ZIO[Has[RouteRepository], Throwable, Seq[RouteDbo]] =
    ZIO.accessM[Has[RouteRepository]](_.get.findByIataDestination(iataCode))

}
