package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Common.testEnv
import com.cmartin.aviation.repository.Model.CountryDbo
import com.cmartin.aviation.repository.TestData._
import com.cmartin.aviation.repository.zioimpl.common.runtime
import zio.Has
import zio.TaskLayer

import java.sql.SQLIntegrityConstraintViolationException

class SlickCountryRepositorySpec
    extends SlickBaseRepositorySpec {

  val env: TaskLayer[Has[CountryRepository]] =
    testEnv >>> SlickCountryRepository.live

  behavior of "SlickCountryRepository"

  "Insert" should "insert a Country into the database" in {
    val program = for {
      id <- SlickCountryRepository.insert(spainDbo)
    } yield id

    val layeredProgram = program.provideLayer(env)
    val id = runtime.unsafeRun(layeredProgram)

    assert(id > 0)
  }

  it should "insert a sequence of Countries into the database" in {
    val program = for {
      ids <- SlickCountryRepository.insert(Seq(spainDbo, portugalDbo))
    } yield ids

    val layeredProgram = program.provideLayer(env)
    val ids = runtime.unsafeRun(layeredProgram)

    assert(ids.forall(_ > 0L), "non positive entity identifier")
  }

  it should "fail to insert a duplicate Country into the database" in {
    val program = for {
      _ <- SlickCountryRepository.insert(spainDbo)
      _ <- SlickCountryRepository.insert(spainDbo)
    } yield ()

    val resultEither = runtime.unsafeRun(program.provideLayer(env).either)

    resultEither.left.value shouldBe a[SQLIntegrityConstraintViolationException]
  }

  "Find" should "retrieve a Country by code" in {
    val program = for {
      _ <- SlickCountryRepository.insert(spainDbo)
      dbo <- SlickCountryRepository.findByCode(spainCode)
    } yield dbo

    val layeredProgram = program.provideLayer(env)
    val dboOpt = runtime.unsafeRun(layeredProgram)

    dboOpt shouldBe Some(spainDbo.copy(id = dboOpt.get.id))
  }

  it should "return None for a missing Country" in {
    val program = for {
      dbo <- SlickCountryRepository.findByCode(spainCode)
    } yield dbo

    val layeredProgram = program.provideLayer(env)
    val dboOpt = runtime.unsafeRun(layeredProgram)

    dboOpt shouldBe None
  }

  "Update" should "update a country retrieved from the database" in {
    val program = for {
      _ <- SlickCountryRepository.insert(spainDbo)
      dbo <- SlickCountryRepository.findByCode(spainCode)
      count <- SlickCountryRepository.update(dbo.get.copy(name = updatedSpainText))
      updated <- SlickCountryRepository.findByCode(spainCode)
    } yield (updated, count)

    val layeredProgram = program.provideLayer(env)
    val (dboOpt, count) = runtime.unsafeRun(layeredProgram)

    dboOpt shouldBe Some(CountryDbo(updatedSpainText, spainCode, dboOpt.get.id))
    count shouldBe 1
  }

  "Delete" should "delete a country from the database" in {
    val program = for {
      id <- SlickCountryRepository.insert(spainDbo)
      count <- SlickCountryRepository.delete(spainDbo.code)
    } yield count

    val count = runtime.unsafeRun(program.provideLayer(env))

    assert(count == 1)
  }

  it should "return zero deleted items for a missing Country" in {
    val program = for {
      cs <- SlickCountryRepository.delete(spainDbo.code)
      count <- SlickCountryRepository.count()
    } yield (cs, count)

    val (cs, count) = runtime.unsafeRun(program.provideLayer(env))

    assert(cs == 0)
    assert(count == 0)
  }

}
