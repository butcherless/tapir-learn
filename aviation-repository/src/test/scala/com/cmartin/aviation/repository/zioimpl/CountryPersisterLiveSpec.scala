package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.Commons
import com.cmartin.aviation.port.CountryPersister
import com.cmartin.aviation.repository.Common.testEnv
import com.cmartin.aviation.repository.TestData._
import com.cmartin.aviation.repository.zioimpl.common.runtime
import zio.Has
import zio.TaskLayer
import zio.URLayer
import zio.ZLayer
import zio.logging.Logging
import com.cmartin.aviation.repository.CountryRepository

class CountryPersisterLiveSpec
    extends SlickBaseRepositorySpec {

  val repoEnv: TaskLayer[Has[CountryRepository]] =
    testEnv >>>
      CountryRepositoryLive.layer

  val env: TaskLayer[Has[CountryPersister]] =
    Commons.loggingEnv ++ repoEnv >>> CountryPersisterLive.layer

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
}
