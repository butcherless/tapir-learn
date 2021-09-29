package com.cmartin.aviation.service

import com.cmartin.aviation.domain.Model._
import zio._
import zio.logging._
import zio.logging.slf4j.Slf4jLogger

object LayerPoc {

  object Repositories {
    trait MyCountryRepository {
      def insert(country: Country): IO[String, Long]
      def findByCode(code: CountryCode): IO[String, Option[Country]]
      def existsByCode(code: CountryCode): IO[String, Boolean]
    }
    object MyCountryRepository {
      def insert(country: Country): ZIO[Has[MyCountryRepository], String, Long] =
        ZIO.serviceWith[MyCountryRepository](_.insert(country))

      def findByCode(code: CountryCode): ZIO[Has[MyCountryRepository], String, Option[Country]] =
        ZIO.serviceWith[MyCountryRepository](_.findByCode(code))

      def existsByCode(code: CountryCode): ZIO[Has[MyCountryRepository], String, Boolean] =
        ZIO.serviceWith[MyCountryRepository](_.existsByCode(code))
    }

    trait MyAirportRepository {
      def insert(airport: Airport): IO[String, Long]
    }
    object MyAirportRepository {
      def insert(airport: Airport): ZIO[Has[MyAirportRepository], String, Long] =
        ZIO.serviceWith[MyAirportRepository](_.insert(airport))
    }
  }

  object RepositoryImplementations {
    import Repositories._

    case class MyCountryRepositoryLive(logging: Logging)
        extends MyCountryRepository {

      override def existsByCode(code: CountryCode): IO[String, Boolean] =
        (
          for {
            _ <- log.debug(s"existsByCode: $code")
            exists <- UIO.succeed(true) // simulation
          } yield exists
        ).provide(logging)

      override def insert(country: Country): IO[String, Long] =
        (
          for {
            _ <- log.debug(s"insert: $country")
            id <- IO.succeed(1L)
          } yield id
        ).provide(logging)

      override def findByCode(code: CountryCode): IO[String, Option[Country]] =
        (
          for {
            _ <- log.debug(s"findByCode: $code")
          } yield Some(Country(code, s"Country-name-for-$code"))
        ).provide(logging)

    }

    object MyCountryRepositoryLive {
      val layer: URLayer[Has[Logging], Has[MyCountryRepository]] =
        (MyCountryRepositoryLive(_)).toLayer
    }

    case class MyAirportRepositoryLive(logging: Logging)
        extends MyAirportRepository {

      override def insert(airport: Airport): IO[String, Long] =
        (
          for {
            _ <- log.debug(s"insert: $airport")
            id <- IO.succeed(1L)
          } yield id
        ).provide(logging)
    }

    object MyAirportRepositoryLive {
      val layer: URLayer[Has[Logging], Has[MyAirportRepository]] =
        (MyAirportRepositoryLive(_)).toLayer
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

    trait MyAirportService {
      def create(airport: Airport): IO[String, Airport]
    }

    object MyAirportService {
      def create(airport: Airport): ZIO[Has[MyAirportService], String, Airport] =
        ZIO.serviceWith[MyAirportService](_.create(airport))
    }
  }

  object ServiceImplementations {
    import Services._
    import Repositories._

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

    case class MyAirportServiceLive(
        logging: Logging,
        countryRepository: MyCountryRepository,
        airportRepository: MyAirportRepository
    ) extends MyAirportService {

      override def create(airport: Airport): IO[String, Airport] = {
        val program = for {
          _ <- log.debug(s"create: $airport")
          _ <- ZIO.ifM(countryRepository.existsByCode(airport.country.code))(
            airportRepository.insert(airport),
            IO.fail(s"Country not found for code: ${airport.country.code}")
          )
        } yield airport

        program.provide(logging)
      }
    }

    private def manageUniqueResult[T](domainOpt: Option[T], message: String): IO[String, T] = {
      domainOpt match {
        case Some(value) => IO.succeed(value)
        case None        => IO.fail(message)
      }
    }

    object MyAirportServiceLive {
      val layer
          : URLayer[Has[Logging] with Has[MyCountryRepository] with Has[MyAirportRepository], Has[MyAirportService]] =
        (MyAirportServiceLive(_, _, _)).toLayer
    }
  }

  /* common infrastructure */
  val loggingEnv: ULayer[Has[Logging]] =
    Slf4jLogger.make((_, message) => message).map(Has(_))
  val runtime = zio.Runtime.default

  /* module use */
  object CountryRepositoryUse {
    import RepositoryImplementations._
    import Repositories._

    val country: Country = ???

    /* Repository Layer
     - requirement: Logging
     - output: Repository Layer
     */
    val countryRepoEnv: ULayer[Has[MyCountryRepository]] =
      (loggingEnv >>> MyCountryRepositoryLive.layer)

    // insert computation 'has' a Repository dependency
    val repositoryProgram: ZIO[Has[MyCountryRepository], String, Long] =
      MyCountryRepository.insert(country)
    val repositoryResult = runtime.unsafeRun(
      repositoryProgram.provideLayer(countryRepoEnv)
    )
  }

  object CountryServiceUse {
    import RepositoryImplementations._
    import Services._
    import ServiceImplementations._

    val country: Country = ???

    /* Service Layer
     - requirement: Logging + Repository
     - output: Service Layer
     */
    val countryServEnv: ULayer[Has[MyCountryService]] =
      loggingEnv >+> MyCountryRepositoryLive.layer >>> MyCountryServiceLive.layer

    val serviceProgram: ZIO[Has[MyCountryService], String, Country] =
      MyCountryService.create(country)
    val serviceResult = runtime.unsafeRun(
      serviceProgram.provideLayer(countryServEnv)
    )
  }

  object AirportServiceUse {
    import ServiceImplementations._
    import Services._
    import RepositoryImplementations._
    import Repositories._

    /* Repositories Layer
     - requirement: Logging
     - output: Logging + RepoA + RepoB Layer
     */
    val repositoriesEnv: ULayer[Has[Logging] with Has[MyCountryRepository] with Has[MyAirportRepository]] =
      loggingEnv >+> MyCountryRepositoryLive.layer ++ MyAirportRepositoryLive.layer

    /* Service Layer
     - requirement: Logging + RepoA + RepoB
     - output: Service Layer
     */
    val airportServEnv: ULayer[Has[MyAirportService]] =
      repositoriesEnv >>> MyAirportServiceLive.layer

    val airport: Airport = ???
    val airportSrvProg: ZIO[Has[MyAirportService], String, Airport] =
      MyAirportService.create(airport)
    val airportSrvRes: Airport = runtime.unsafeRun(
      airportSrvProg.provideLayer(airportServEnv)
    )
  }

}
