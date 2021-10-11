package com.cmartin.aviation.repository

import com.cmartin.aviation.repository.Model.RouteDbo
import zio.Has
import zio.IO
import zio.ZIO

trait RouteRepository
    extends BaseRepository[RouteDbo] {

  def findByIataOrigin(iataCode: String): IO[Throwable, Seq[RouteDbo]]
  def findByIataDestination(iataCode: String): IO[Throwable, Seq[RouteDbo]]
  def findByOriginAndDestination(iataOrigin: String, iataDestination: String): IO[Throwable, Option[RouteDbo]]
  def deleteByOriginAndDestination(iataOrigin: String, iataDestination: String): IO[Throwable, Int]
}

object RouteRepository {
  def insert(dbo: RouteDbo): ZIO[Has[RouteRepository], Throwable, Long] =
    ZIO.accessM[Has[RouteRepository]](_.get.insert(dbo))

  def insert(seq: Seq[RouteDbo]): ZIO[Has[RouteRepository], Throwable, Seq[Long]] =
    ZIO.accessM[Has[RouteRepository]](_.get.insertSeq(seq))

  def update(dbo: RouteDbo): ZIO[Has[RouteRepository], Throwable, Int] =
    ZIO.accessM[Has[RouteRepository]](_.get.update(dbo))

  def findByOriginAndDestination(
      iataOrigin: String,
      iataDestination: String
  ): ZIO[Has[RouteRepository], Throwable, Option[RouteDbo]] =
    ZIO.accessM[Has[RouteRepository]](_.get.findByOriginAndDestination(iataOrigin, iataDestination))

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
