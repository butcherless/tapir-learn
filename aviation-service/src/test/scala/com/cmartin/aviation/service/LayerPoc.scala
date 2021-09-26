package com.cmartin.aviation.service

import com.cmartin.aviation.domain.Model.Country
import zio._
import zio.logging._
import zio.logging.slf4j.Slf4jLogger

object LayerPoc {

  object Repositories {
    trait MyCountryRepository {
      def insert(country: Country): IO[String, Long]
    }
    object MyCountryRepository {
      def insert(country: Country): ZIO[Has[MyCountryRepository], String, Long] =
        ZIO.serviceWith[MyCountryRepository](_.insert(country))
    }
  }

  object RepositoryImplementations {
    import Repositories.MyCountryRepository

    case class MyCountryRepositoryLive(logging: Logging)
        extends MyCountryRepository {

      override def insert(country: Country): IO[String, Long] =
        (
          for {
            _ <- log.debug(s"insert: $country")
            id <- IO.succeed(1L)
          } yield id
        ).provide(logging)
    }

    object MyCountryRepositoryLive {
      val layer: URLayer[Has[Logging], Has[MyCountryRepository]] =
        (MyCountryRepositoryLive(_)).toLayer
    }
  }

  object Services {
    trait MyCountryService {
      def create(country: Country): IO[String, Country]
    }

    object MyCountryService {
      def create(country: Country): ZIO[Has[MyCountryService], String, Country] =
        ZIO.serviceWith[MyCountryService](_.create(country))
    }
  }

  object ServiceImplementations {
    import Services.MyCountryService
    import Repositories.MyCountryRepository

    case class MyCountryServiceLive(logging: Logging, countryRepository: MyCountryRepository)
        extends MyCountryService {

      override def create(country: Country): IO[String, Country] = {
        val program = for {
          _ <- log.debug(s"create: $country")
          id <- countryRepository.insert(country)
        } yield country

        program.provide(logging)
      }
    }

    object MyCountryServiceLive {
      val layer: URLayer[Has[Logging] with Has[MyCountryRepository], Has[MyCountryService]] =
        (MyCountryServiceLive(_, _)).toLayer
    }
  }

  import RepositoryImplementations._
  import ServiceImplementations._
  import Repositories._
  import Services._

  val runtime = zio.Runtime.default
  val country: Country = ???
  val loggingEnv: ZLayer[Any, Nothing, Has[Logging]] =
    Slf4jLogger.make((_, message) => message).map(Has(_))

  val countryRepoEnv: ZLayer[Any, Nothing, Has[MyCountryRepository]] =
    (loggingEnv >>> MyCountryRepositoryLive.layer)

  // insert computation 'has' a Repository dependency
  val repositoryProgram: ZIO[Has[MyCountryRepository], String, Long] =
    MyCountryRepository.insert(country)
  val repositoryResult = runtime.unsafeRun(
    repositoryProgram.provideLayer(countryRepoEnv)
  )

  val srvL = loggingEnv ++ countryRepoEnv >>> MyCountryServiceLive.layer

  val serviceProgram: ZIO[Has[MyCountryService], String, Country] =
    MyCountryService.create(country)
  val serviceResult = runtime.unsafeRun(
    serviceProgram.provideLayer(srvL)
  )

}
