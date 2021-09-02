package com.cmartin.aviation.repository
import scala.concurrent.Await

import Common.dao
import TestData._
import Model._
import org.scalatest.Inside._

class RouteSlickRepositorySpec extends BaseRepositorySpec {
  import dao.runAction

  behavior of "Route Slick Repository"

  "Insert" should "insert a Route into the repository" in {
    val result = for {
      countryId <- dao.countryRepository.insert(spainDbo)
      originId <- dao.airportRepository.insert(madDbo.copy(countryId = countryId))
      destinationId <- dao.airportRepository.insert(bcnDbo.copy(countryId = countryId))
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
        originId <- dao.airportRepository.insert(madDbo.copy(countryId = countryId))
        destinationId <- dao.airportRepository.insert(bcnDbo.copy(countryId = countryId))
        _ <- dao.routeRepository.insert(RouteDbo(262.0, originId, destinationId))
        _ <- dao.routeRepository.insert(RouteDbo(262.0, originId, destinationId))
      } yield ()
    }
  }

  it should "fail to insert a Route with missing origin airport into the repository" in {
    recoverToSucceededIf[java.sql.SQLException] {
      for {
        countryId <- dao.countryRepository.insert(spainDbo)
        destinationId <- dao.airportRepository.insert(bcnDbo.copy(countryId = countryId))
        _ <- dao.routeRepository.insert(RouteDbo(262.0, 0L, destinationId))
      } yield ()
    }
  }

  it should "fail to insert a Route with missing destination airport into the repository" in {
    recoverToSucceededIf[java.sql.SQLException] {
      for {
        countryId <- dao.countryRepository.insert(spainDbo)
        originId <- dao.airportRepository.insert(madDbo.copy(countryId = countryId))
        _ <- dao.routeRepository.insert(RouteDbo(262.0, originId, 0L))
      } yield ()
    }
  }

  "Find" should "retrieve a Route sequence by origin airport" in {
    val result = for {
      countryId <- dao.countryRepository.insert(spainDbo)
      madId <- dao.airportRepository.insert(madDbo.copy(countryId = countryId))
      bcnId <- dao.airportRepository.insert(bcnDbo.copy(countryId = countryId))
      tfnId <- dao.airportRepository.insert(tfnDbo.copy(countryId = countryId))
      _ <- insertRoundTripRoute(262.0, madId, bcnId)
      _ <- insertRoundTripRoute(957.0, madId, tfnId)
      _ <- insertRoundTripRoute(1185.0, bcnId, tfnId)
      seq <- dao.routeRepository.findByIataOrigin(madIataCode)
    } yield (seq, madId)

    result map {
      case (seq, madId) =>
        seq.map { route =>
          inside(route) {
            case RouteDbo(_, originId, _, _) =>
              originId shouldBe madId
          }
        }
        seq should have size 2
    }
  }

  "Find" should "retrieve a Route sequence by destination airport" in {
    val result = for {
      countryId <- dao.countryRepository.insert(spainDbo)
      madId <- dao.airportRepository.insert(madDbo.copy(countryId = countryId))
      bcnId <- dao.airportRepository.insert(bcnDbo.copy(countryId = countryId))
      tfnId <- dao.airportRepository.insert(tfnDbo.copy(countryId = countryId))
      _ <- insertRoundTripRoute(262.0, madId, bcnId)
      _ <- insertRoundTripRoute(957.0, madId, tfnId)
      _ <- insertRoundTripRoute(1185.0, bcnId, tfnId)
      seq <- dao.routeRepository.findByIataDestination(bcnIataCode)
    } yield (seq, bcnId)

    result map {
      case (seq, bcnId) =>
        seq.map { route =>
          info(s"$route")
          inside(route) {
            case RouteDbo(_, _, destinationId, _) =>
              destinationId shouldBe bcnId
          }
        }
        seq should have size 2
    }
  }

  private def insertRoundTripRoute(distance: Double, origin: Long, destination: Long) =
    for {
      _ <- dao.routeRepository.insert(RouteDbo(distance, origin, destination))
      _ <- dao.routeRepository.insert(RouteDbo(distance, destination, origin))
    } yield ()

  override protected def beforeEach(): Unit = {
    Await.result(dao.createSchema(), waitTimeout)
  }

  override protected def afterEach(): Unit = {
    Await.result(dao.dropSchema(), waitTimeout)
  }
}
