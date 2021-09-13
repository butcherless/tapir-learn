package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Common.testEnv
import com.cmartin.aviation.repository.Model.AirportDbo
import com.cmartin.aviation.repository.TestData._
import com.cmartin.aviation.repository.zioimpl.common.runtime
import zio.{Has, TaskLayer}

import java.sql.SQLIntegrityConstraintViolationException

class SlickAirportRepositorySpec
    extends SlickBaseRepositorySpec {

  val env: TaskLayer[Has[CountryRepository] with Has[AirportRepository]] =
    (testEnv >>> SlickCountryRepository.live) ++
      (testEnv >>> SlickAirportRepository.live)

  "Insert" should "insert a Country into the database" in {

    val program = for {
      countryId <- SlickCountryRepository.insert(spainDbo)
      airportId <- SlickAirportRepository.insert(madDbo.copy(countryId = countryId))
    } yield airportId

    val layeredProgram = program.provideLayer(env)
    val id = runtime.unsafeRun(layeredProgram)

    assert(id > 0)
  }

  it should "fail to insert a duplicate Country into the database" in {
    val program = for {
      countryId <- SlickCountryRepository.insert(spainDbo)
      _ <- SlickAirportRepository.insert(madDbo.copy(countryId = countryId))
      _ <- SlickAirportRepository.insert(madDbo.copy(countryId = countryId))
    } yield ()

    val resultEither = runtime.unsafeRun(program.provideLayer(env).either)

    resultEither.isLeft shouldBe true
    resultEither.swap.map {
      case _: SQLIntegrityConstraintViolationException => succeed
      case _                                           => fail("unexpected error")
    }
  }

  "Find" should "retrieve an Airport by iata code" in {
    val program = for {
      countryId <- SlickCountryRepository.insert(spainDbo)
      aid <- SlickAirportRepository.insert(madDbo.copy(countryId = countryId))
      airport <- SlickAirportRepository.findByIataCode(madIataCode)
    } yield (airport, countryId, aid)
    val layeredProgram = program.provideLayer(env)
    val (dboOpt, cid, aid) = runtime.unsafeRun(layeredProgram)

    dboOpt shouldBe Some(madDbo.copy(countryId = cid, id = Some(aid)))
  }

  it should "retrieve an Airport by icao code" in {
    val program = for {
      countryId <- SlickCountryRepository.insert(spainDbo)
      aid <- SlickAirportRepository.insert(madDbo.copy(countryId = countryId))
      airport <- SlickAirportRepository.findByIcaoCode(madIcaoCode)
    } yield (airport, countryId, aid)
    val layeredProgram = program.provideLayer(env)
    val (dboOpt, cid, aid) = runtime.unsafeRun(layeredProgram)

    dboOpt shouldBe Some(madDbo.copy(countryId = cid, id = Some(aid)))
  }

  it should "retrieve a sequence of Airports by country code" in {
    val program = for {
      spainId <- SlickCountryRepository.insert(spainDbo)
      portugalId <- SlickCountryRepository.insert(portugalDbo)
      _ <- SlickAirportRepository.insert(madDbo.copy(countryId = spainId))
      _ <- SlickAirportRepository.insert(bcnDbo.copy(countryId = spainId))
      _ <- SlickAirportRepository.insert(lisDbo.copy(countryId = portugalId))
      airports <- SlickAirportRepository.findByCountryCode(spainCode)
      count <- SlickAirportRepository.count()
    } yield (airports, count)
    val layeredProgram = program.provideLayer(env)
    val (airports, count) = runtime.unsafeRun(layeredProgram)

    airports should have size 2
    count shouldBe 3
  }

  it should "retrieve a sequence of airports with a similar name" in {
    val airportOne = AirportDbo("Barajas", "md1", "lem1")
    val airportTwo = AirportDbo("Madrid Barajas", "md2", "lem2")
    val airportThree = AirportDbo("Adolfo Suárez Madrid Barajas", "md3", "lem3")

    val program = for {
      id <- SlickCountryRepository.insert(spainDbo)
      _ <- SlickAirportRepository.insert(airportOne.copy(countryId = id))
      _ <- SlickAirportRepository.insert(airportTwo.copy(countryId = id))
      _ <- SlickAirportRepository.insert(airportThree.copy(countryId = id))
      airports <- SlickAirportRepository.findByName("Barajas")
    } yield airports

    val layeredProgram = program.provideLayer(env)
    val airports = runtime.unsafeRun(layeredProgram)

    airports should have size 3

  }

  it should "return None for a missing Airport" in {
    val program = for {
      airport <- SlickAirportRepository.findByIataCode(madIataCode)
    } yield (airport)
    val layeredProgram = program.provideLayer(env)
    val dboOpt = runtime.unsafeRun(layeredProgram)

    dboOpt shouldBe None
  }

  "Update" should "update an Airport retrieved from the database" in {
    val program = for {
      id <- SlickCountryRepository.insert(spainDbo)
      _ <- SlickAirportRepository.insert(madDbo.copy(countryId = id))
      dbo <- SlickAirportRepository.findByIataCode(madIataCode)
      count <- SlickAirportRepository.update(dbo.get.copy(name = updatedMadText))
      updated <- SlickAirportRepository.findByIataCode(madIataCode)
    } yield (updated, count)

    val (updated, count) = runtime.unsafeRun(program.provideLayer(env))

    count shouldBe 1
    updated.isDefined shouldBe true
    updated.get.name shouldBe updatedMadText
  }

  "Delete" should "delete an Airport from the database" in {
    val program = for {
      id <- SlickCountryRepository.insert(spainDbo)
      _ <- SlickAirportRepository.insert(madDbo.copy(countryId = id))
      count <- SlickAirportRepository.delete(madIataCode)
    } yield count

    val count = runtime.unsafeRun(program.provideLayer(env))

    assert(count == 1)
  }

}
