package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Model.RouteDbo
import zio.IO

trait RouteRepository
    extends BaseRepository[RouteDbo] {

  def findByIataOrigin(iataCode: String): IO[Throwable, Seq[RouteDbo]]
  def findByIataDestination(iataCode: String): IO[Throwable, Seq[RouteDbo]]
  def findByOriginAndDestination(iataOrigin: String, iataDestination: String): IO[Throwable, Option[RouteDbo]]
  def deleteByOriginAndDestination(iataOrigin: String, iataDestination: String): IO[Throwable, Int]
}
