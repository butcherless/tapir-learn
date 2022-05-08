package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Model.AirportDbo
import com.cmartin.aviation.repository.TestData._
import com.cmartin.aviation.repository.{AirportRepository, Common, CountryRepository}
import zio.Runtime.{default => runtime}
import zio.ZLayer.Debug
import zio.{TaskLayer, ZLayer}

import java.sql.SQLIntegrityConstraintViolationException

class SlickAirportRepositorySpec
    extends SlickBaseRepositorySpec {

  val env: TaskLayer[CountryRepository with AirportRepository] =
    ZLayer.make[CountryRepository with AirportRepository](
      Common.dbLayer,
      SlickCountryRepository.layer,
      SlickAirportRepository.layer,
      Debug.mermaid
    )

  behavior of "SlickAirportRepository"

  "Insert" should "insert a Country into the database" in {

    val program = for {
      countryId <- CountryRepository.insert(spainDbo)
      airportId <- AirportRepository.insert(madDbo.copy(countryId = countryId))
    } yield airportId

    val id = runtime.unsafeRun(
      program.provideLayer(env)
    )

    id should be > 0L
  }

  it should "insert a sequence of Airports into the database" in {

    val program = for {
      countryId <- CountryRepository.insert(spainDbo)
      ids <- AirportRepository.insert(
        Seq(madDbo.copy(countryId = countryId), bcnDbo.copy(countryId = countryId))
      )
    } yield ids

    val ids = runtime.unsafeRun(
      program.provideLayer(env)
    )

    assert(ids.forall(_ > 0L), "non positive entity identifier")
  }

  it should "fail to insert a duplicate Country into the database" in {
    val program = for {
      countryId <- CountryRepository.insert(spainDbo)
      _ <- AirportRepository.insert(madDbo.copy(countryId = countryId))
      _ <- AirportRepository.insert(madDbo.copy(countryId = countryId))
    } yield ()

    val resultEither = runtime.unsafeRun(
      program.provideLayer(env).either
    )

    resultEither.left.value shouldBe a[SQLIntegrityConstraintViolationException]
  }

  "Find" should "retrieve an Airport by iata code" in {
    val program = for {
      countryId <- CountryRepository.insert(spainDbo)
      aid <- AirportRepository.insert(madDbo.copy(countryId = countryId))
      airport <- AirportRepository.findByIataCode(madIataCode)
    } yield (airport, countryId, aid)

    val (dboOpt, cid, aid) = runtime.unsafeRun(
      program.provideLayer(env)
    )

    dboOpt shouldBe Some(madDbo.copy(countryId = cid, id = aid))
  }

  it should "retrieve an Airport by icao code" in {
    val program = for {
      countryId <- CountryRepository.insert(spainDbo)
      aid <- AirportRepository.insert(madDbo.copy(countryId = countryId))
      airport <- AirportRepository.findByIcaoCode(madIcaoCode)
    } yield (airport, countryId, aid)

    val (dboOpt, cid, aid) = runtime.unsafeRun(
      program.provideLayer(env)
    )

    dboOpt shouldBe Some(madDbo.copy(countryId = cid, id = aid))
  }

  it should "retrieve a sequence of Airports by country code" in {
    val program = for {
      spainId <- CountryRepository.insert(spainDbo)
      portugalId <- CountryRepository.insert(portugalDbo)
      _ <- AirportRepository.insert(madDbo.copy(countryId = spainId))
      _ <- AirportRepository.insert(bcnDbo.copy(countryId = spainId))
      _ <- AirportRepository.insert(lisDbo.copy(countryId = portugalId))
      airports <- AirportRepository.findByCountryCode(spainCode)
      count <- AirportRepository.count()
    } yield (airports, spainId, count)

    val (airports, spainId, count) = runtime.unsafeRun(
      program.provideLayer(env)
    )

    airports should have size 2
    airports.forall(_.countryId == spainId) shouldBe true
    count shouldBe 3
  }

  it should "retrieve a sequence of airports with a similar name" in {
    val airportOne = AirportDbo("Barajas", "md1", "lem1")
    val airportTwo = AirportDbo("Madrid Barajas", "md2", "lem2")
    val airportThree = AirportDbo("Adolfo Suárez Madrid Barajas", "md3", "lem3")

    val program = for {
      id <- CountryRepository.insert(spainDbo)
      _ <- AirportRepository.insert(airportOne.copy(countryId = id))
      _ <- AirportRepository.insert(airportTwo.copy(countryId = id))
      _ <- AirportRepository.insert(airportThree.copy(countryId = id))
      airports <- AirportRepository.findByName("Barajas")
    } yield airports

    val airports = runtime.unsafeRun(
      program.provideLayer(env)
    )

    airports should have size 3
  }

  it should "return None for a missing Airport" in {
    val program = for {
      airport <- AirportRepository.findByIataCode(madIataCode)
    } yield airport

    val dboOpt = runtime.unsafeRun(
      program.provideLayer(env)
    )

    dboOpt shouldBe None
  }

  "Update" should "update an Airport retrieved from the database" in {
    val program = for {
      id <- CountryRepository.insert(spainDbo)
      _ <- AirportRepository.insert(madDbo.copy(countryId = id))
      dbo <- AirportRepository.findByIataCode(madIataCode)
      count <- AirportRepository.update(dbo.get.copy(name = updatedMadText))
      updated <- AirportRepository.findByIataCode(madIataCode)
    } yield (updated, count)

    val (updated, count) = runtime.unsafeRun(
      program.provideLayer(env)
    )

    count shouldBe 1
    updated.isDefined shouldBe true
    updated.get.name shouldBe updatedMadText
  }

  "Delete" should "delete an Airport from the database" in {
    val program = for {
      id <- CountryRepository.insert(spainDbo)
      _ <- AirportRepository.insert(madDbo.copy(countryId = id))
      count <- AirportRepository.deleteByIataCode(madIataCode)
    } yield count

    val count = runtime.unsafeRun(
      program.provideLayer(env)
    )

    count shouldBe 1
  }

}
