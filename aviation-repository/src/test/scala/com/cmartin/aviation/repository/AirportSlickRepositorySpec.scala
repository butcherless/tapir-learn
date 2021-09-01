package com.cmartin.aviation.repository

import org.scalatest.Inside._
import slick.dbio.DBIO

import scala.concurrent.Await

import Common.dao
import TestData._
import Model._

class AirportSlickRepositorySpec extends BaseRepositorySpec {
  import dao.runAction

  behavior of "Airport Slick Repository"

  "Insert" should "insert an Airport into the repository" in {
    val result = for {
      id <- dao.insertAirport(spainDbo)(madDbo)
      count <- dao.airportRepository.count()
    } yield (id, count)

    result map {
      case (id, count) =>
        assert(id > 0)
        count shouldBe 1
    }
  }

  it should "insert a sequence of airports into the database" in {
    val result = for {
      countryId <- dao.countryRepository.insert(spainDbo)
      cs <- dao.airportRepository.insert(airportSequence.map(a => updateCountryId(a)(countryId)))
    } yield cs

    result map { cs =>
      assert(cs.nonEmpty, "empty sequence")
      assert(cs.size == countrySequence.size, "invalid sequence size")
      assert(cs.forall(_ > 0L), "non positive entity identifier")
    }
  }

  it should "fail to insert a duplicate Airport into the repository" in {
    recoverToSucceededIf[java.sql.SQLException] {
      for {
        countryId <- dao.countryRepository.insert(spainDbo)
        _ <- dao.airportRepository.insert(updateCountryId(madDbo)(countryId))
        _ <- dao.airportRepository.insert(updateCountryId(madDbo)(countryId))
      } yield ()
    }
  }

  "Update" should "update a country from the database" in {
    val result = for {
      id <- insertAirport(spainDbo)(madDbo)
      created <- dao.airportRepository.findById(Option(id))
      _ <- dao.airportRepository.update(created.get.copy(name = updatedMadText))
      updated <- dao.airportRepository.findById(Option(id))
    } yield (id, updated)

    result map {
      case (id, airport) =>
        assert(id > 0L)
        assert(airport.isDefined)
        inside(airport.get) {
          case AirportDbo(name, _, _, _, _) =>
            name shouldBe updatedMadText
        }
    }
  }

  "Delete" should "delete a country from the database" in {
    val result = for {
      id <- insertAirport(spainDbo)(madDbo)
      did <- dao.airportRepository.delete(id)
      count <- dao.airportRepository.count()
    } yield (id, did, count)

    result map {
      case (id, dCount, count) =>
        assert(id > 0L)
        assert(dCount == 1)
        assert(count == 0)
    }
  }

  "Find" should "retrieve an airport by its iata code" in {
    val result = for {
      id <- insertAirport(spainDbo)(madDbo)
      airport <- dao.airportRepository.findByIataCode(madIataCode)
    } yield airport

    result map { airport =>
      assert(airport.isDefined)
      inside(airport.get) {
        case AirportDbo(_, iataCode, _, _, _) =>
          iataCode shouldBe madIataCode
      }
    }
  }

  it should "retrieve an airport by its icao code" in {
    val result = for {
      id <- insertAirport(spainDbo)(madDbo)
      airport <- dao.airportRepository.findByIcaoCode(madIcaoCode)
    } yield airport

    result map { airport =>
      assert(airport.isDefined)
      inside(airport.get) {
        case AirportDbo(_, _, icaoCode, _, _) =>
          icaoCode shouldBe madIcaoCode
      }
    }
  }

  it should "retrieve an airport sequence by its country code" in {
    val result = for {
      esId <- dao.countryRepository.insert(spainDbo)
      ptId <- dao.countryRepository.insert(portugalDbo)
      _ <- dao.airportRepository.insert(airportSequence.map(a => updateCountryId(a)(esId)))
      _ <- dao.airportRepository.insert(updateCountryId(lisDbo)(ptId))
      airports <- dao.airportRepository.findByCountryCode(spainCode)
      count <- dao.airportRepository.count()
    } yield (airports, count)

    result map {
      case (airports, count) =>
        airports should have size 2
        count shouldBe 3
    }
  }

  def insertAirport(countryDbo: CountryDbo)(airportDbo: AirportDbo): DBIO[Long] = {
    for {
      countryId <- dao.countryRepository.insert(countryDbo)
      id <- dao.airportRepository.insert(updateCountryId(airportDbo)(countryId))
    } yield id
  }

  def updateCountryId(dbo: AirportDbo)(countryId: Long): AirportDbo =
    dbo.copy(countryId = countryId)

  override protected def beforeEach(): Unit = {
    Await.result(dao.createSchema(), waitTimeout)
  }

  override protected def afterEach(): Unit = {
    Await.result(dao.dropSchema(), waitTimeout)
  }
}
