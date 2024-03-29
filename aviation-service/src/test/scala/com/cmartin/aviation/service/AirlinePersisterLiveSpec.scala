package com.cmartin.aviation.service

import com.cmartin.aviation.domain.Model._
import com.cmartin.aviation.port.{AirlinePersister, CountryPersister}
import com.cmartin.aviation.repository.AirlineRepository
import com.cmartin.aviation.repository.zioimpl.{SlickAirlineRepository, SlickCountryRepository}
import com.cmartin.aviation.test.Common
import com.cmartin.aviation.test.TestData._
import zio.{ZIO, ZLayer}

class AirlinePersisterLiveSpec
    extends SlickBasePersisterSpec {

  import TestRepositories._

  val env =
    ZLayer.make[CountryPersister with AirlinePersister](
      Common.dbLayer,
      SlickCountryRepository.layer,
      SlickAirlineRepository.layer,
      CountryPersisterLive.layer,
      AirlinePersisterLive.layer
    )

  // Simulator for database infrastructure exceptions
  val airlineRepoMock = mock[AirlineRepository]
  val mockEnv         =
    ZLayer.make[CountryPersister with AirlinePersister](
      Common.dbLayer,
      SlickCountryRepository.layer,
      ZLayer.succeed(airlineRepoMock),
      CountryPersisterLive.layer,
      AirlinePersisterLive.layer
    )

  behavior of "AirlinePersisterLive"

  "Insert" should "insert an Airline into the database" in {
    val program = for {
      _  <- CountryPersister.insert(spainCountry)
      id <- AirlinePersister.insert(ibeAirline)
    } yield id

    val id = unsafeRun(program.provideLayer(env))

    id should be > 0L
  }

  it should "fail to insert a duplicate Airline into the database" in {
    val program = for {
      _ <- CountryPersister.insert(spainCountry)
      _ <- AirlinePersister.insert(ibeAirline)
      _ <- AirlinePersister.insert(ibeAirline)
    } yield ()

    val either = unsafeRun(program.provideLayer(env).either)
    either.left.value shouldBe a[DuplicateEntityError]
  }

  "Exists" should "return true for an existing Airline" in {
    val program = for {
      _      <- CountryPersister.insert(spainCountry)
      _      <- AirlinePersister.insert(ibeAirline)
      exists <- AirlinePersister.existsByCode(ibeIataCode)
    } yield exists

    val exists = unsafeRun(program.provideLayer(env))

    exists shouldBe true
  }

  it should "return false for a missing Airline" in {
    val program = for {
      exists <- AirlinePersister.existsByCode(ibeIataCode)
    } yield exists

    val exists = unsafeRun(program.provideLayer(env))

    exists shouldBe false
  }

  it should "manage a database exception: existsByCode" in {
    // GIVEN
    (airlineRepoMock.findByIataCode _)
      .expects(ibeIataCode)
      .returns(TestRepositories.failDefault())
      .once()

    // WHEN
    val program = for {
      _ <- AirlinePersister.existsByCode(ibeIataCode)
    } yield ()

    val either = unsafeRun(program.provideLayer(mockEnv).either)

    either.left.value shouldBe a[UnexpectedServiceError]
  }

  "Find" should "retrive an Airline by its code" in {
    val program = for {
      _          <- CountryPersister.insert(spainCountry)
      _          <- AirlinePersister.insert(ibeAirline)
      airlineOpt <- AirlinePersister.findByCode(ibeIataCode)
    } yield airlineOpt

    val airlineOpt = unsafeRun(program.provideLayer(env))

    airlineOpt shouldBe Some(ibeAirline)
  }

  it should "retrieve None for a missing Airline" in {
    val program = for {
      airlineOpt <- AirlinePersister.findByCode(ibeIataCode)
    } yield airlineOpt

    val airlineOpt = unsafeRun(program.provideLayer(env))

    airlineOpt shouldBe None
  }

  it should "manage a database exception: findByCode" in {
    // GIVEN
    (airlineRepoMock.findByIataCode _)
      .expects(ibeIataCode)
      .returns(TestRepositories.failDefault())
      .once()

    val program = for {
      _ <- AirlinePersister.findByCode(ibeIataCode)
    } yield ()

    val either = unsafeRun(program.provideLayer(mockEnv).either)

    either.left.value shouldBe a[UnexpectedServiceError]
  }

  it should "retrieve a sequence of airlines that belong to a country" in {
    val program = for {
      _        <- CountryPersister.insert(spainCountry)
      _        <- AirlinePersister.insert(ibeAirline)
      _        <- AirlinePersister.insert(aeaAirline)
      airlines <- AirlinePersister.findByCountry(spainCode)
    } yield airlines

    val airlines = unsafeRun(program.provideLayer(env))

    airlines shouldBe Seq(ibeAirline, aeaAirline)
  }

  it should "retrieve an empty sequence for a missing Country" in {
    val program = for {
      airlines <- AirlinePersister.findByCountry(spainCode)
    } yield airlines

    val airlines = unsafeRun(program.provideLayer(env))

    airlines shouldBe empty
  }

  it should "manage a database exception: findByCountry" in {
    // GIVEN
    (airlineRepoMock.findByCountryCode _)
      .expects(spainCode)
      .returns(TestRepositories.failDefault())
      .once()

    val program = for {
      _ <- AirlinePersister.findByCountry(spainCode)
    } yield ()

    val either = unsafeRun(program.provideLayer(mockEnv).either)

    either.left.value shouldBe a[UnexpectedServiceError]
  }

  "Update" should "update an Airline" in {
    val updatedAirline = Airline(updatedIbeText, IataCode("ib"), IcaoCode("IBE"), ibeFoundationDate, spainCountry)

    val program = for {
      _          <- CountryPersister.insert(spainCountry)
      id         <- AirlinePersister.insert(ibeAirline)
      count      <- AirlinePersister.update(updatedAirline)
      airlineOpt <- AirlinePersister.findByCode(ibeIataCode)
    } yield (airlineOpt, count)

    val (airlineOpt, count) = unsafeRun(program.provideLayer(env))

    count shouldBe 1
    airlineOpt shouldBe Some(updatedAirline)
  }

  it should "manage a database exception: update" in {
    // GIVEN
    (airlineRepoMock.insert _)
      .expects(ibeDbo.copy(countryId = 1L))
      .returns(ZIO.succeed(1L))

    (airlineRepoMock.findByIataCode _)
      .expects(ibeIataCode)
      .returns(ZIO.some(ibeDbo.copy(countryId = 1L)))

    (airlineRepoMock.update _)
      .expects(*)
      .returns(TestRepositories.failDefault())

    val updatedAirline = Airline(updatedIbeText, IataCode("ib"), IcaoCode("IBE"), ibeFoundationDate, spainCountry)

    val program = for {
      _ <- CountryPersister.insert(spainCountry)
      _ <- AirlinePersister.insert(ibeAirline)
      _ <- AirlinePersister.update(updatedAirline)
    } yield ()

    val either = unsafeRun(program.provideLayer(mockEnv).either)

    either.left.value shouldBe a[UnexpectedServiceError]
  }

  "Delete" should "delete an Airline by its iata code" in {
    val program = for {
      _     <- CountryPersister.insert(spainCountry)
      _     <- AirlinePersister.insert(ibeAirline)
      count <- AirlinePersister.delete(ibeIataCode)
    } yield count

    val count = unsafeRun(program.provideLayer(env))

    count shouldBe 1
  }

  it should "manage a database exception: delete" in {
    // GIVEN
    (airlineRepoMock.deleteByIataCode _)
      .expects(ibeIataCode)
      .returns(TestRepositories.failDefault())
      .once()

    val program = for {
      _ <- AirlinePersister.delete(ibeIataCode)
    } yield ()

    val either = unsafeRun(program.provideLayer(mockEnv).either)

    either.left.value shouldBe a[UnexpectedServiceError]
  }
}
