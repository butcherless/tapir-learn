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

  override protected def beforeEach(): Unit = {
    Await.result(dao.createSchema(), waitTimeout)
  }

  override protected def afterEach(): Unit = {
    Await.result(dao.dropSchema(), waitTimeout)
  }
}
