package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Common.testEnv
import com.cmartin.aviation.repository.Model.AirlineDbo
import com.cmartin.aviation.repository.TestData._
import com.cmartin.aviation.repository.zioimpl.common.runtime
import zio.Has
import zio.TaskLayer
import zio.ZLayer

import java.sql.SQLIntegrityConstraintViolationException
import java.time.LocalDate

class SlickAirlineRepositorySpec
    extends SlickBaseRepositorySpec {

  val env: TaskLayer[Has[CountryRepository] with Has[AirlineRepository]] =
    (testEnv >>> SlickCountryRepository.live) ++
      (testEnv >>> SlickAirlineRepository.live)

  behavior of "SlickAirlineRepository"

  "Insert" should "insert an Airline into the database" in {

    val program = for {
      countryId <- SlickCountryRepository.insert(spainDbo)
      airlineId <- SlickAirlineRepository.insert(ibeDbo.copy(countryId = countryId))
    } yield airlineId

    val id = runtime.unsafeRun(
      program.provideLayer(env)
    )

    id should be > 0L
  }

  it should "insert a sequence of Airlines into the database" in {

    val program = for {
      countryId <- SlickCountryRepository.insert(spainDbo)
      ids <- SlickAirlineRepository.insert(
        Seq(ibeDbo.copy(countryId = countryId), aeaDbo.copy(countryId = countryId))
      )
    } yield ids

    val ids = runtime.unsafeRun(
      program.provideLayer(env)
    )

    assert(ids.forall(_ > 0L), "non positive entity identifier")
  }

  it should "fail to insert a duplicate Airline into the database" in {
    val program = for {
      countryId <- SlickCountryRepository.insert(spainDbo)
      _ <- SlickAirlineRepository.insert(ibeDbo.copy(countryId = countryId))
      _ <- SlickAirlineRepository.insert(ibeDbo.copy(countryId = countryId))
    } yield ()

    val resultEither = runtime.unsafeRun(
      program.provideLayer(env).either
    )

    resultEither.left.value shouldBe a[SQLIntegrityConstraintViolationException]
  }

  "Find" should "retrieve an Airline by iata code" in {
    val program = for {
      countryId <- SlickCountryRepository.insert(spainDbo)
      aid <- SlickAirlineRepository.insert(ibeDbo.copy(countryId = countryId))
      airline <- SlickAirlineRepository.findByIataCode(ibeIataCode)
    } yield (airline, countryId, aid)

    val (dboOpt, cid, aid) = runtime.unsafeRun(
      program.provideLayer(env)
    )

    dboOpt shouldBe Some(ibeDbo.copy(countryId = cid, id = Some(aid)))
  }

  it should "retrieve an Airline by icao code" in {
    val program = for {
      countryId <- SlickCountryRepository.insert(spainDbo)
      aid <- SlickAirlineRepository.insert(ibeDbo.copy(countryId = countryId))
      airline <- SlickAirlineRepository.findByIcaoCode(ibeIcaoCode)
    } yield (airline, countryId, aid)
    val layeredProgram = program.provideLayer(env)
    val (dboOpt, cid, aid) = runtime.unsafeRun(layeredProgram)

    dboOpt shouldBe Some(ibeDbo.copy(countryId = cid, id = Some(aid)))
  }

  it should "retrieve a sequence of Airlines by country code" in {
    val program = for {
      spainId <- SlickCountryRepository.insert(spainDbo)
      portugalId <- SlickCountryRepository.insert(portugalDbo)
      _ <- SlickAirlineRepository.insert(ibeDbo.copy(countryId = spainId))
      _ <- SlickAirlineRepository.insert(aeaDbo.copy(countryId = spainId))
      _ <- SlickAirlineRepository.insert(tapDbo.copy(countryId = portugalId))
      airlines <- SlickAirlineRepository.findByCountryCode(spainCode)
      count <- SlickAirlineRepository.count()
    } yield (airlines, spainId, count)

    val (airlines, spainId, count) = runtime.unsafeRun(
      program.provideLayer(env)
    )

    airlines should have size 2
    airlines.forall(_.countryId == spainId) shouldBe true
    count shouldBe 3
  }

  it should "retrieve a sequence of airlines with a similar name" in {
    val airlineOne = AirlineDbo("Iberia", "iata1", "icao1", LocalDate.now())
    val airlineTwo = AirlineDbo("Compañia Aérea Iberia", "iata2", "icao2", LocalDate.now())
    val airlineThree = AirlineDbo("Aeronaves de los cielos de Iberia", "iata3", "icao3", LocalDate.now())

    val program = for {
      id <- SlickCountryRepository.insert(spainDbo)
      _ <- SlickAirlineRepository.insert(airlineOne.copy(countryId = id))
      _ <- SlickAirlineRepository.insert(airlineTwo.copy(countryId = id))
      _ <- SlickAirlineRepository.insert(airlineThree.copy(countryId = id))
      airlines <- SlickAirlineRepository.findByName("Iberia")
    } yield airlines

    val airlines = runtime.unsafeRun(
      program.provideLayer(env)
    )

    airlines should have size 3
  }

  it should "return None for a missing Airline" in {
    val program = for {
      airline <- SlickAirlineRepository.findByIataCode(ibeIataCode)
    } yield (airline)

    val dboOpt = runtime.unsafeRun(
      program.provideLayer(env)
    )

    dboOpt shouldBe None
  }

  "Update" should "update an Airport retrieved from the database" in {
    val program = for {
      id <- SlickCountryRepository.insert(spainDbo)
      _ <- SlickAirlineRepository.insert(ibeDbo.copy(countryId = id))
      dbo <- SlickAirlineRepository.findByIataCode(ibeIataCode)
      count <- SlickAirlineRepository.update(dbo.get.copy(name = updatedIbeText))
      updated <- SlickAirlineRepository.findByIataCode(ibeIataCode)
    } yield (updated, count)

    val (updated, count) = runtime.unsafeRun(
      program.provideLayer(env)
    )

    count shouldBe 1
    updated.isDefined shouldBe true
    updated.get.name shouldBe updatedIbeText
  }

  "Delete" should "delete an Airline from the database" in {
    val program = for {
      countryId <- SlickCountryRepository.insert(spainDbo)
      _ <- SlickAirlineRepository.insert(ibeDbo.copy(countryId = countryId))
      count <- SlickAirlineRepository.deleteByIataCode(ibeIataCode)
    } yield count

    val count = runtime.unsafeRun(
      program.provideLayer(env)
    )

    count shouldBe 1
  }
}
