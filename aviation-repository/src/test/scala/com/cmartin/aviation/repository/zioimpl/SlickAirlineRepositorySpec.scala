package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Model.AirlineDbo
import com.cmartin.aviation.repository.TestData._
import com.cmartin.aviation.repository.{AirlineRepository, Common, CountryRepository}
import zio.Runtime.{default => runtime}
import zio.ZLayer.Debug
import zio.{TaskLayer, ZLayer}

import java.sql.SQLIntegrityConstraintViolationException
import java.time.LocalDate

class SlickAirlineRepositorySpec
    extends SlickBaseRepositorySpec {

  val env: TaskLayer[CountryRepository with AirlineRepository] =
    ZLayer.make[CountryRepository with AirlineRepository](
      Common.dbLayer,
      SlickCountryRepository.layer,
      SlickAirlineRepository.layer,
      Debug.mermaid
    )

  behavior of "SlickAirlineRepository"

  "Insert" should "insert an Airline into the database" in {

    val program = for {
      countryId <- CountryRepository.insert(spainDbo)
      airlineId <- AirlineRepository.insert(ibeDbo.copy(countryId = countryId))
    } yield airlineId

    val id = runtime.unsafeRun(
      program.provideLayer(env)
    )

    id should be > 0L
  }

  it should "insert a sequence of Airlines into the database" in {

    val program = for {
      countryId <- CountryRepository.insert(spainDbo)
      ids <- AirlineRepository.insert(
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
      countryId <- CountryRepository.insert(spainDbo)
      _ <- AirlineRepository.insert(ibeDbo.copy(countryId = countryId))
      _ <- AirlineRepository.insert(ibeDbo.copy(countryId = countryId))
    } yield ()

    val resultEither = runtime.unsafeRun(
      program.provideLayer(env).either
    )

    resultEither.left.value shouldBe a[SQLIntegrityConstraintViolationException]
  }

  "Find" should "retrieve an Airline by iata code" in {
    val program = for {
      countryId <- CountryRepository.insert(spainDbo)
      aid <- AirlineRepository.insert(ibeDbo.copy(countryId = countryId))
      airline <- AirlineRepository.findByIataCode(ibeIataCode)
    } yield (airline, countryId, aid)

    val (dboOpt, cid, aid) = runtime.unsafeRun(
      program.provideLayer(env)
    )

    dboOpt shouldBe Some(ibeDbo.copy(countryId = cid, id = aid))
  }

  it should "retrieve an Airline by icao code" in {
    val program = for {
      countryId <- CountryRepository.insert(spainDbo)
      aid <- AirlineRepository.insert(ibeDbo.copy(countryId = countryId))
      airline <- AirlineRepository.findByIcaoCode(ibeIcaoCode)
    } yield (airline, countryId, aid)
    val layeredProgram = program.provideLayer(env)
    val (dboOpt, cid, aid) = runtime.unsafeRun(layeredProgram)

    dboOpt shouldBe Some(ibeDbo.copy(countryId = cid, id = aid))
  }

  it should "retrieve a sequence of Airlines by country code" in {
    val program = for {
      spainId <- CountryRepository.insert(spainDbo)
      portugalId <- CountryRepository.insert(portugalDbo)
      _ <- AirlineRepository.insert(ibeDbo.copy(countryId = spainId))
      _ <- AirlineRepository.insert(aeaDbo.copy(countryId = spainId))
      _ <- AirlineRepository.insert(tapDbo.copy(countryId = portugalId))
      airlines <- AirlineRepository.findByCountryCode(spainCode)
      count <- AirlineRepository.count()
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
      id <- CountryRepository.insert(spainDbo)
      _ <- AirlineRepository.insert(airlineOne.copy(countryId = id))
      _ <- AirlineRepository.insert(airlineTwo.copy(countryId = id))
      _ <- AirlineRepository.insert(airlineThree.copy(countryId = id))
      airlines <- AirlineRepository.findByName("Iberia")
    } yield airlines

    val airlines = runtime.unsafeRun(
      program.provideLayer(env)
    )

    airlines should have size 3
  }

  it should "return None for a missing Airline" in {
    val program = for {
      airline <- AirlineRepository.findByIataCode(ibeIataCode)
    } yield airline

    val dboOpt = runtime.unsafeRun(
      program.provideLayer(env)
    )

    dboOpt shouldBe None
  }

  "Update" should "update an Airport retrieved from the database" in {
    val program = for {
      id <- CountryRepository.insert(spainDbo)
      _ <- AirlineRepository.insert(ibeDbo.copy(countryId = id))
      dbo <- AirlineRepository.findByIataCode(ibeIataCode)
      count <- AirlineRepository.update(dbo.get.copy(name = updatedIbeText))
      updated <- AirlineRepository.findByIataCode(ibeIataCode)
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
      countryId <- CountryRepository.insert(spainDbo)
      _ <- AirlineRepository.insert(ibeDbo.copy(countryId = countryId))
      count <- AirlineRepository.deleteByIataCode(ibeIataCode)
    } yield count

    val count = runtime.unsafeRun(
      program.provideLayer(env)
    )

    count shouldBe 1
  }
}
