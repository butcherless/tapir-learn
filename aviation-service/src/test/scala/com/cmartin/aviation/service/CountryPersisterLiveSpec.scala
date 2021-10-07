package com.cmartin.aviation.service

import com.cmartin.aviation.Commons
import com.cmartin.aviation.domain.Model._
import com.cmartin.aviation.port.CountryPersister
import com.cmartin.aviation.repository.Common.testEnv
import com.cmartin.aviation.repository.CountryRepository
import com.cmartin.aviation.repository.TestData._
import com.cmartin.aviation.repository.zioimpl.common.runtime
import zio.Has
import zio.TaskLayer
import zio.ZLayer
import com.cmartin.aviation.repository.zioimpl.CountryRepositoryLive

class CountryPersisterLiveSpec
    extends SlickBaseRepositorySpec {

  val env: TaskLayer[Has[CountryPersister]] =
    testEnv >>>
      CountryRepositoryLive.layer ++
      Commons.loggingEnv >>> CountryPersisterLive.layer

  // Simulator for database infrastructure exceptions
  val mockEnv: TaskLayer[Has[CountryPersister]] =
    ZLayer.succeed(TestRepositories.countryRepository) ++
      Commons.loggingEnv >>> CountryPersisterLive.layer

  behavior of "CountryPersisterLive"

  "Insert" should "insert a Country into the database" in {
    val program = for {
      id <- CountryPersister.insert(spainCountry)
    } yield id

    val id = runtime.unsafeRun(
      program.provideLayer(env)
    )

    id should be > 0L
  }

  it should "fail to insert a duplicate Country into the database" in {
    val program = for {
      _ <- CountryPersister.insert(spainCountry)
      _ <- CountryPersister.insert(spainCountry)
    } yield ()

    val either = runtime.unsafeRun(
      program.provideLayer(env).either
    )
    either.left.value shouldBe a[DuplicateEntityError]
  }

  "Exists" should "return true for an existing Country" in {
    val program = for {
      _ <- CountryPersister.insert(spainCountry)
      exists <- CountryPersister.existsByCode(spainCode)
    } yield exists

    val exists = runtime.unsafeRun(
      program.provideLayer(env)
    )

    exists shouldBe true
  }

  it should "return false for a missing Country" in {
    val program = for {
      exists <- CountryPersister.existsByCode(spainCode)
    } yield exists

    val exists = runtime.unsafeRun(
      program.provideLayer(env)
    )

    exists shouldBe false
  }

  it should "manage a database exception: existsByCode" in {
    val program = for {
      _ <- CountryPersister.existsByCode(spainCode)
    } yield ()

    val either = runtime.unsafeRun(
      program.provideLayer(mockEnv).either
    )

    either.left.value shouldBe a[UnexpectedServiceError]
  }

  "Find" should "retrive a Country by its code" in {
    val program = for {
      _ <- CountryPersister.insert(spainCountry)
      countryOpt <- CountryPersister.findByCode(spainCode)
    } yield countryOpt

    val countryOpt = runtime.unsafeRun(
      program.provideLayer(env)
    )

    countryOpt shouldBe Some(spainCountry)
  }

  it should "retrive None for a missing Country" in {
    val program = for {
      countryOpt <- CountryPersister.findByCode(spainCode)
    } yield countryOpt

    val countryOpt = runtime.unsafeRun(
      program.provideLayer(env)
    )

    countryOpt shouldBe None
  }

  it should "manage a database exception: findByCode" in {
    val program = for {
      _ <- CountryPersister.findByCode(spainCode)
    } yield ()

    val either = runtime.unsafeRun(
      program.provideLayer(mockEnv).either
    )

    either.left.value shouldBe a[UnexpectedServiceError]
  }

  "Update" should "update a Country" in {
    val updatedCountry = Country(spainCode, updatedSpainText)
    val program = for {
      _ <- CountryPersister.insert(spainCountry)
      count <- CountryPersister.update(updatedCountry)
      countryOpt <- CountryPersister.findByCode(spainCode)
    } yield (countryOpt, count)

    val (countryOpt, count) = runtime.unsafeRun(
      program.provideLayer(env)
    )

    count shouldBe 1
    countryOpt shouldBe Some(updatedCountry)
  }

  it should "manage a database exception: update" in {
    val updatedCountry = Country(spainCode, updatedSpainText)
    val program = for {
      _ <- CountryPersister.update(updatedCountry)
    } yield ()

    val either = runtime.unsafeRun(
      program.provideLayer(env).either
    )

    either.left.value shouldBe a[MissingEntityError]
  }

  "Delete" should "delete a Country by its code" in {
    val program = for {
      _ <- CountryPersister.insert(spainCountry)
      count <- CountryPersister.delete(spainCode)
    } yield count

    val count = runtime.unsafeRun(
      program.provideLayer(env)
    )

    count shouldBe 1
  }

  it should "manage a database exception: delete" in {
    val program = for {
      _ <- CountryPersister.delete(spainCode)
    } yield ()

    val either = runtime.unsafeRun(
      program.provideLayer(mockEnv).either
    )

    either.left.value shouldBe a[UnexpectedServiceError]
  }
}
