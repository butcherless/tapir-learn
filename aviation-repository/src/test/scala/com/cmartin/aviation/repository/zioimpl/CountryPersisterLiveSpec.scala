package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.Commons
import com.cmartin.aviation.domain.Model._
import com.cmartin.aviation.port.CountryPersister
import com.cmartin.aviation.repository.Common.testEnv
import com.cmartin.aviation.repository.CountryRepository
import com.cmartin.aviation.repository.TestData._
import com.cmartin.aviation.repository.zioimpl.common.runtime
import zio.Has
import zio.TaskLayer

class CountryPersisterLiveSpec
    extends SlickBaseRepositorySpec {

  val env: TaskLayer[Has[CountryPersister]] =
    testEnv >>>
      CountryRepositoryLive.layer ++
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
    either.left.value shouldBe a[UnexpectedServiceError]
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

}
