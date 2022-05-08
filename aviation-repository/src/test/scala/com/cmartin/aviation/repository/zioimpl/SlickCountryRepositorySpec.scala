package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Common
import com.cmartin.aviation.repository.CountryRepository
import com.cmartin.aviation.repository.Model.CountryDbo
import com.cmartin.aviation.repository.TestData._
import zio.Runtime.{default => runtime}
import zio.TaskLayer
import zio.ZLayer
import zio.ZLayer.Debug

import java.sql.SQLIntegrityConstraintViolationException

class SlickCountryRepositorySpec
    extends SlickBaseRepositorySpec {

  import SlickCountryRepositorySpec._

  behavior of "SlickCountryRepository"

  "Insert" should "insert a Country into the database" in {
    val program = for {
      id <- CountryRepository.insert(spainDbo)
    } yield id

    val id = runtime.unsafeRun(
      program.provideLayer(env)
    )

    id should be > 0L
  }

  it should "insert a sequence of Countries into the database" in {
    val program = for {
      ids <- CountryRepository.insert(Seq(spainDbo, portugalDbo))
    } yield ids

    val ids = runtime.unsafeRun(
      program.provideLayer(env)
    )

    assert(ids.forall(_ > 0L), "non positive entity identifier")
  }

  it should "fail to insert a duplicate Country into the database" in {
    val program = for {
      _ <- CountryRepository.insert(spainDbo)
      _ <- CountryRepository.insert(spainDbo)
    } yield ()

    val resultEither = runtime.unsafeRun(
      program.provideLayer(env).either
    )

    resultEither.left.value shouldBe a[SQLIntegrityConstraintViolationException]
  }

  "Find" should "retrieve a Country by code" in {
    val program = for {
      _ <- CountryRepository.insert(spainDbo)
      dbo <- CountryRepository.findByCode(spainCode)
    } yield dbo

    val dboOpt = runtime.unsafeRun(
      program.provideLayer(env)
    )

    dboOpt shouldBe Some(spainDbo.copy(id = dboOpt.get.id))
  }

  it should "return None for a missing Country" in {
    val program = for {
      dbo <- CountryRepository.findByCode(spainCode)
    } yield dbo

    val dboOpt = runtime.unsafeRun(
      program.provideLayer(env)
    )

    dboOpt shouldBe None
  }

  "Update" should "update a country retrieved from the database" in {
    val program = for {
      _ <- CountryRepository.insert(spainDbo)
      dbo <- CountryRepository.findByCode(spainCode)
      count <- CountryRepository.update(dbo.get.copy(name = updatedSpainText))
      updated <- CountryRepository.findByCode(spainCode)
    } yield (updated, count)

    val (dboOpt, count) = runtime.unsafeRun(
      program.provideLayer(env)
    )

    dboOpt shouldBe Some(CountryDbo(updatedSpainText, spainCode, dboOpt.get.id))
    count shouldBe 1
  }

  "Delete" should "delete a country from the database" in {
    val program = for {
      _ <- CountryRepository.insert(spainDbo)
      count <- CountryRepository.delete(spainDbo.code)
    } yield count

    val count = runtime.unsafeRun(
      program.provideLayer(env)
    )

    count shouldBe 1
  }

  it should "return zero deleted items for a missing Country" in {
    val program = for {
      cs <- CountryRepository.delete(spainDbo.code)
      count <- CountryRepository.count()
    } yield (cs, count)

    val (cs, count) = runtime.unsafeRun(
      program.provideLayer(env)
    )

    cs shouldBe 0
    count shouldBe 0
  }

}

object SlickCountryRepositorySpec {
  val env: TaskLayer[CountryRepository] =
    ZLayer.make[CountryRepository](
      Common.dbLayer,
      SlickCountryRepository.layer
    )

}
