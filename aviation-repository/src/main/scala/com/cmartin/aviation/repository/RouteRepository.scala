package com.cmartin.aviation.repository

import com.cmartin.aviation.repository.Model.RouteDbo
import com.cmartin.aviation.repository.zioimpl.CommonAbstractions.Repository.BaseRepository
import zio.{IO, ZIO}

trait RouteRepository
    extends BaseRepository[RouteDbo] {

  def findByIataOrigin(iataCode: String): IO[Throwable, Seq[RouteDbo]]
  def findByIataDestination(iataCode: String): IO[Throwable, Seq[RouteDbo]]
  def findByOriginAndDestination(iataOrigin: String, iataDestination: String): IO[Throwable, Option[RouteDbo]]
  def deleteByOriginAndDestination(iataOrigin: String, iataDestination: String): IO[Throwable, Int]
}

object RouteRepository {
  def insert(dbo: RouteDbo): ZIO[RouteRepository, Throwable, Long] =
    ZIO.serviceWithZIO[RouteRepository](_.insert(dbo))

  def insert(seq: Seq[RouteDbo]): ZIO[RouteRepository, Throwable, Seq[Long]] =
    ZIO.serviceWithZIO[RouteRepository](_.insertSeq(seq))

  def update(dbo: RouteDbo): ZIO[RouteRepository, Throwable, Int] =
    ZIO.serviceWithZIO[RouteRepository](_.update(dbo))

  def findByOriginAndDestination(
      iataOrigin: String,
      iataDestination: String
  ): ZIO[RouteRepository, Throwable, Option[RouteDbo]] =
    ZIO.serviceWithZIO[RouteRepository](_.findByOriginAndDestination(iataOrigin, iataDestination))

  def deleteByOriginAndDestination(
      iataOrigin: String,
      iataDestination: String
  ): ZIO[RouteRepository, Throwable, Int] =
    ZIO.serviceWithZIO[RouteRepository](_.deleteByOriginAndDestination(iataOrigin, iataDestination))

  def findByIataOrigin(iataCode: String): ZIO[RouteRepository, Throwable, Seq[RouteDbo]] =
    ZIO.serviceWithZIO[RouteRepository](_.findByIataOrigin(iataCode))

  def findByIataDestination(iataCode: String): ZIO[RouteRepository, Throwable, Seq[RouteDbo]] =
    ZIO.serviceWithZIO[RouteRepository](_.findByIataDestination(iataCode))

}
