package com.cmartin.aviation.service

import com.cmartin.aviation.Commons
import com.cmartin.aviation.domain.Model._
import com.cmartin.aviation.port.AirlinePersister
import com.cmartin.aviation.port.CountryPersister
import com.cmartin.aviation.repository.Common.testEnv
import com.cmartin.aviation.repository.TestData._
import com.cmartin.aviation.repository.zioimpl.AirlineRepositoryLive
import com.cmartin.aviation.repository.zioimpl.CountryRepositoryLive
import com.cmartin.aviation.repository.zioimpl.common.runtime
import zio.Has
import zio.TaskLayer

class AirlinePersisterLiveSpec
    extends SlickBaseRepositorySpec {

  val env: TaskLayer[Has[CountryPersister] with Has[AirlinePersister]] =
    testEnv >>>
      CountryRepositoryLive.layer ++
      AirlineRepositoryLive.layer ++
      Commons.loggingEnv >>>
      CountryPersisterLive.layer ++
      AirlinePersisterLive.layer

  behavior of "AirlinePersisterLive"

  "Insert" should "insert an Airline into the database" in {
    val program = for {
      _ <- CountryPersister.insert(spainCountry)
      id <- AirlinePersister.insert(ibeAirline)
    } yield id

    val id = runtime.unsafeRun(
      program.provideLayer(env)
    )

    id should be > 0L
  }

  it should "fail to insert a duplicate Airline into the database" in {
    val program = for {
      _ <- CountryPersister.insert(spainCountry)
      _ <- AirlinePersister.insert(ibeAirline)
      _ <- AirlinePersister.insert(ibeAirline)
    } yield ()

    val either = runtime.unsafeRun(
      program.provideLayer(env).either
    )
    either.left.value shouldBe a[DuplicateEntityError]
  }

  "Exists" should "return true for an existing Airline" in {
    val program = for {
      _ <- CountryPersister.insert(spainCountry)
      _ <- AirlinePersister.insert(ibeAirline)
      exists <- AirlinePersister.existsByCode(ibeIataCode)
    } yield exists

    val exists = runtime.unsafeRun(
      program.provideLayer(env)
    )

    exists shouldBe true
  }

  it should "return false for a missing Airline" in {
    val program = for {
      exists <- AirlinePersister.existsByCode(ibeIataCode)
    } yield exists

    val exists = runtime.unsafeRun(
      program.provideLayer(env)
    )

    exists shouldBe false
  }

  "Find" should "retrive an Airline by its code" in {
    val program = for {
      _ <- CountryPersister.insert(spainCountry)
      _ <- AirlinePersister.insert(ibeAirline)
      airlineOpt <- AirlinePersister.findByCode(ibeIataCode)
    } yield airlineOpt

    val airlineOpt = runtime.unsafeRun(
      program.provideLayer(env)
    )

    airlineOpt shouldBe Some(ibeAirline)
  }

  it should "retrieve None for a missing Airline" in {
    val program = for {
      airlineOpt <- AirlinePersister.findByCode(ibeIataCode)
    } yield airlineOpt

    val airlineOpt = runtime.unsafeRun(
      program.provideLayer(env)
    )

    airlineOpt shouldBe None
  }

  it should "retrieve an empty sequence for a missing Country" in {
    val program = for {
      airlines <- AirlinePersister.findByCountry(spainCode)
    } yield airlines

    val airlines = runtime.unsafeRun(
      program.provideLayer(env)
    )

    airlines shouldBe empty
  }

  "Delete" should "delete an Airline by its iata code" in {
    val program = for {
      _ <- CountryPersister.insert(spainCountry)
      _ <- AirlinePersister.insert(ibeAirline)
      count <- AirlinePersister.delete(ibeIataCode)
    } yield count

    val count = runtime.unsafeRun(
      program.provideLayer(env)
    )

    count shouldBe 1
  }
}
