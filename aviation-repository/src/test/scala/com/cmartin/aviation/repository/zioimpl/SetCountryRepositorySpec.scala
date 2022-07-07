package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.CountryRepository
import com.cmartin.aviation.repository.Model.CountryDbo
import com.cmartin.aviation.repository.TestData._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.Runtime.{default => runtime}
import zio.stm.{STM, TRef, TSet}
import zio.{TaskLayer, ZIO, ZLayer}

class SetCountryRepositorySpec
    extends AnyFlatSpec
    with Matchers
    with BeforeAndAfterEach {

  import SetCountryRepositorySpec._

  behavior of "MapCountryRepository"

  "Insert" should "insert a Country into the database" in {
    val program = for {
      id    <- CountryRepository.insert(spainDbo)
      count <- CountryRepository.count()
    } yield (id, count)

    val (id, count) = runtime.unsafeRun(program.provideLayer(env))

    id should be > 0L
    count shouldBe 1
  }

  "Find" should "retrieve a Country by code from the database" in {
    val program = for {
      id  <- CountryRepository.insert(spainDbo)
      dbo <- CountryRepository.findByCode(spainCode)
    } yield dbo

    val dboOpt = runtime.unsafeRun(program.provideLayer(env))

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
      id      <- CountryRepository.insert(spainDbo)
      dbo     <- CountryRepository.find(id)
      count   <- CountryRepository.update(dbo.get.copy(name = updatedSpainText))
      updated <- CountryRepository.find(id)
    } yield (updated, count)

    val (dboOpt, count) = runtime.unsafeRun(
      program.provideLayer(env)
    )

    dboOpt shouldBe Some(CountryDbo(updatedSpainText, spainCode, dboOpt.get.id))
    count shouldBe 1
  }

  "Delete" should "delete a country from the database" in {
    val program = for {
      id    <- CountryRepository.insert(spainDbo)
      count <- CountryRepository.delete(spainCode)
    } yield count

    val count = runtime.unsafeRun(program.provideLayer(env))

    count shouldBe 1
  }

  it should "return zero deleted items for a missing Country" in {
    val program = for {
      count <- CountryRepository.delete(spainCode)
    } yield count

    val count = runtime.unsafeRun(program.provideLayer(env))

    count shouldBe 0
  }
  override def beforeEach(): Unit = {
    runtime.unsafeRun(deleteDbSet())
  }
}

object SetCountryRepositorySpec {

  lazy val (seqTRef, dbTSet) = runtime.unsafeRun {
    for {
      tuple                         <- STM.atomically {
                                         for {
                                           tRef       <- TRef.make(0)
                                           sequential <- tRef.get
                                           tSet       <- TSet.empty[CountryDbo]
                                           size       <- tSet.size
                                         } yield (tRef, tSet, sequential, size)
                                       }
      (tRef, tSet, sequential, size) = tuple
      _                             <- ZIO.log(s"initializing in memory Set Repository: (sequential,size)=($sequential,$size)")
    } yield (tRef, tSet)
  }

  val env: TaskLayer[CountryRepository] =
    ZLayer.make[CountryRepository](
      ZLayer.succeed(seqTRef),
      ZLayer.succeed(dbTSet),
      SetCountryRepository.layer
    )

  def deleteDbSet() =
    STM.atomically {
      for {
        countries <- dbTSet.toSet
        _         <- dbTSet.deleteAll(countries)
      } yield ()
    }
}
