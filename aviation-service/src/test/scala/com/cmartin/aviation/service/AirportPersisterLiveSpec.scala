package com.cmartin.aviation.service

import com.cmartin.aviation.Commons
import com.cmartin.aviation.domain.Model._
import com.cmartin.aviation.port.AirportPersister
import com.cmartin.aviation.port.CountryPersister
import com.cmartin.aviation.repository.AirportRepository
import com.cmartin.aviation.repository.Common.testEnv
import com.cmartin.aviation.repository.TestData._
import com.cmartin.aviation.repository.zioimpl.AirportRepositoryLive
import com.cmartin.aviation.repository.zioimpl.CountryRepositoryLive
import zio.Runtime.{default => runtime}
import zio.Has
import zio.Task
import zio.TaskLayer
import zio.ZLayer

class AirportPersisterLiveSpec
    extends SlickBasePersisterSpec {

  val env: TaskLayer[Has[CountryPersister] with Has[AirportPersister]] =
    testEnv >>>
      CountryRepositoryLive.layer ++
      AirportRepositoryLive.layer ++
      Commons.loggingEnv >>>
      CountryPersisterLive.layer ++
      AirportPersisterLive.layer

  // Simulator for database infrastructure exceptions
  val airportRepoMock = mock[AirportRepository]
  val mockEnv =
    testEnv >>>
      CountryRepositoryLive.layer ++
      ZLayer.succeed(airportRepoMock) ++
      Commons.loggingEnv >>>
      CountryPersisterLive.layer ++
      AirportPersisterLive.layer

  behavior of "AirportPersisterLive"

  "Insert" should "insert an Airport into the database" in {
    val program = for {
      _ <- CountryPersister.insert(spainCountry)
      id <- AirportPersister.insert(madAirport)
    } yield id

    val id = runtime.unsafeRun(
      program.provideLayer(env)
    )

    id should be > 0L
  }

  it should "fail to insert a duplicate Country into the database" in {
    val program = for {
      _ <- CountryPersister.insert(spainCountry)
      _ <- AirportPersister.insert(madAirport)
      _ <- AirportPersister.insert(madAirport)
    } yield ()

    val either = runtime.unsafeRun(
      program.provideLayer(env).either
    )
    either.left.value shouldBe a[DuplicateEntityError]
  }

  "Exists" should "return true for an existing Airport" in {
    val program = for {
      _ <- CountryPersister.insert(spainCountry)
      _ <- AirportPersister.insert(madAirport)
      exists <- AirportPersister.existsByCode(madIataCode)
    } yield exists

    val exists = runtime.unsafeRun(
      program.provideLayer(env)
    )

    exists shouldBe true
  }

  it should "return false for a missing Airport" in {
    val program = for {
      exists <- AirportPersister.existsByCode(madIataCode)
    } yield exists

    val exists = runtime.unsafeRun(
      program.provideLayer(env)
    )

    exists shouldBe false
  }

  it should "manage a database exception: existsByCode" in {
    //GIVEN
    (airportRepoMock.findByIataCode _)
      .expects(madIataCode)
      .returns(TestRepositories.failDefault())
      .once()

    val program = for {
      _ <- AirportPersister.existsByCode(madIataCode)
    } yield ()

    val either = runtime.unsafeRun(
      program.provideLayer(mockEnv).either
    )

    either.left.value shouldBe a[UnexpectedServiceError]
  }

  "Find" should "retrive an Airport by its code" in {
    val program = for {
      _ <- CountryPersister.insert(spainCountry)
      _ <- AirportPersister.insert(madAirport)
      airportOpt <- AirportPersister.findByCode(madIataCode)
    } yield airportOpt

    val airportOpt = runtime.unsafeRun(
      program.provideLayer(env)
    )

    airportOpt shouldBe Some(madAirport)
  }

  it should "retrieve None for a missing Airport" in {
    val program = for {
      airportOpt <- AirportPersister.findByCode(madIataCode)
    } yield airportOpt

    val airportOpt = runtime.unsafeRun(
      program.provideLayer(env)
    )

    airportOpt shouldBe None
  }

  it should "manage a database exception: findByCode" in {
    //GIVEN
    (airportRepoMock.findByIataCode _)
      .expects(madIataCode)
      .returns(TestRepositories.failDefault())
      .once()

    val program = for {
      _ <- AirportPersister.findByCode(madIataCode)
    } yield ()

    val either = runtime.unsafeRun(
      program.provideLayer(mockEnv).either
    )

    either.left.value shouldBe a[UnexpectedServiceError]
  }

  "Update" should "update an Airport" in {
    val updatedAirport = Airport(updatedMadText, IataCode("MAD"), IcaoCode("lemd"), spainCountry)

    val program = for {
      _ <- CountryPersister.insert(spainCountry)
      id <- AirportPersister.insert(madAirport)
      count <- AirportPersister.update(updatedAirport)
      airportOpt <- AirportPersister.findByCode(madIataCode)
    } yield (airportOpt, count)

    val (airportOpt, count) = runtime.unsafeRun(
      program.provideLayer(env)
    )

    count shouldBe 1
    airportOpt shouldBe Some(updatedAirport)
  }

  it should "manage a database exception: update" in {
    // GIVEN
    (airportRepoMock.insert _)
      .expects(madDbo.copy(countryId = 1L))
      .returns(Task.succeed(1L))

    (airportRepoMock.findByIataCode _)
      .expects(madIataCode)
      .returns(Task.some(madDbo.copy(countryId = 1L)))

    (airportRepoMock.update _)
      .expects(*)
      .returns(TestRepositories.failDefault())

    val updatedAirport = Airport(updatedMadText, IataCode("MAD"), IcaoCode("lemd"), spainCountry)

    val program = for {
      _ <- CountryPersister.insert(spainCountry)
      _ <- AirportPersister.insert(madAirport)
      _ <- AirportPersister.update(updatedAirport)
    } yield ()

    val either = runtime.unsafeRun(
      program.provideLayer(mockEnv).either
    )

    either.left.value shouldBe a[UnexpectedServiceError]
  }

  "Delete" should "delete an Airport by its iata code" in {
    val program = for {
      _ <- CountryPersister.insert(spainCountry)
      _ <- AirportPersister.insert(madAirport)
      count <- AirportPersister.delete(madIataCode)
    } yield count

    val count = runtime.unsafeRun(
      program.provideLayer(env)
    )

    count shouldBe 1
  }

  it should "manage a database exception: delete" in {
    //GIVEN
    (airportRepoMock.deleteByIataCode _)
      .expects(madIataCode)
      .returns(TestRepositories.failDefault())
      .once()

    val program = for {
      _ <- AirportPersister.delete(madIataCode)
    } yield ()

    val either = runtime.unsafeRun(
      program.provideLayer(mockEnv).either
    )

    either.left.value shouldBe a[UnexpectedServiceError]
  }
}
