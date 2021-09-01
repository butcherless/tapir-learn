package com.cmartin.aviation.repository
import scala.concurrent.Await

import Common.dao
import TestData._
import Model._

class RouteSlickRepositorySpec extends BaseRepositorySpec {
  import dao.runAction

  behavior of "Route Slick Repository"

  "Insert" should "insert a Route into the repository" in {
    val result = for {
      countryId <- dao.countryRepository.insert(spainDbo)
      originId <- dao.airportRepository.insert(dao.updateCountryId(madDbo)(countryId))
      destinationId <- dao.airportRepository.insert(dao.updateCountryId(bcnDbo)(countryId))
      id <- dao.routeRepository.insert(RouteDbo(262.0, originId, destinationId))
    } yield id

    result map { id =>
      assert(id > 0)
    }
  }

  it should "fail to insert a duplicate Route into the repository" in {
    recoverToSucceededIf[java.sql.SQLException] {
      for {
        countryId <- dao.countryRepository.insert(spainDbo)
        originId <- dao.airportRepository.insert(dao.updateCountryId(madDbo)(countryId))
        destinationId <- dao.airportRepository.insert(dao.updateCountryId(bcnDbo)(countryId))
        _ <- dao.routeRepository.insert(RouteDbo(262.0, originId, destinationId))
        _ <- dao.routeRepository.insert(RouteDbo(262.0, originId, destinationId))
      } yield ()
    }
  }

  it should "fail to insert a Route with missing origin airport into the repository" in {
    recoverToSucceededIf[java.sql.SQLException] {
      for {
        countryId <- dao.countryRepository.insert(spainDbo)
        destinationId <- dao.airportRepository.insert(dao.updateCountryId(bcnDbo)(countryId))
        _ <- dao.routeRepository.insert(RouteDbo(262.0, 0L, destinationId))
      } yield ()
    }
  }

  it should "fail to insert a Route with missing destination airport into the repository" in {
    recoverToSucceededIf[java.sql.SQLException] {
      for {
        countryId <- dao.countryRepository.insert(spainDbo)
        originId <- dao.airportRepository.insert(dao.updateCountryId(madDbo)(countryId))
        _ <- dao.routeRepository.insert(RouteDbo(262.0, originId, 0L))
      } yield ()
    }
  }

  override protected def beforeEach(): Unit = {
    Await.result(dao.createSchema(), waitTimeout)
  }

  override protected def afterEach(): Unit = {
    Await.result(dao.dropSchema(), waitTimeout)
  }
}
