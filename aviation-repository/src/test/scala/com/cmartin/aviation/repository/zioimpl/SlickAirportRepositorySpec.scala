package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Common.testEnv
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

  it should "return None for a missing Airport" in {
    val program = for {
      airport <- SlickAirportRepository.findByIataCode(madIataCode)
    } yield (airport)
    val layeredProgram = program.provideLayer(env)
    val dboOpt = runtime.unsafeRun(layeredProgram)

    dboOpt shouldBe None
  }

}
