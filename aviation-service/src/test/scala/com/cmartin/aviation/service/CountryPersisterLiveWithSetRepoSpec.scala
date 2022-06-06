package com.cmartin.aviation.service

import com.cmartin.aviation.port.CountryPersister
import com.cmartin.aviation.repository.CountryRepository
import com.cmartin.aviation.repository.Model.CountryDbo
import com.cmartin.aviation.repository.zioimpl.SetCountryRepository
import com.cmartin.aviation.test.TestData._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.Runtime.{default => runtime}
import zio.stm.{STM, TRef, TSet}
import zio.{TaskLayer, ZIO, ZLayer}

class CountryPersisterLiveWithSetRepoSpec
    extends AnyFlatSpec
    with Matchers {
  import CountryPersisterLiveWithSetRepoSpec.countryEnv

  behavior of "CountryPersisterLive"

  val env: TaskLayer[CountryPersister] =
    ZLayer.make[CountryPersister](
      countryEnv,
      CountryPersisterLive.layer
    )

  "Insert" should "TODO: insert a Country into the database" in {
    val program = for {
      id <- CountryPersister.insert(spainCountry)
    } yield id

    val id = runtime.unsafeRun(
      program.provideLayer(env)
    )

    id should be > 0L
  }

}

object CountryPersisterLiveWithSetRepoSpec {
  lazy val (seqTRef, dbTSet) = runtime.unsafeRun {
    for {
      tuple <- STM.atomically {
        for {
          tRef <- TRef.make(0)
          sequential <- tRef.get
          tSet <- TSet.empty[CountryDbo]
          size <- tSet.size
        } yield (tRef, tSet, sequential, size)
      }
      (tRef, tSet, sequential, size) = tuple
      _ <- ZIO.log(s"initializing in memory Set Repository: (sequential,size)=($sequential,$size)")
    } yield (tRef, tSet)
  }

  val countryEnv: TaskLayer[CountryRepository] =
    ZLayer.make[CountryRepository](
      ZLayer.succeed(seqTRef),
      ZLayer.succeed(dbTSet),
      SetCountryRepository.layer
    )

  def deleteDbSet() =
    STM.atomically {
      for {
        countries <- dbTSet.toSet
        _ <- dbTSet.deleteAll(countries)
      } yield ()
    }
}
