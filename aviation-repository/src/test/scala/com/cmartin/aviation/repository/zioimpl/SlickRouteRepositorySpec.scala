package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Common._
import com.cmartin.aviation.repository.Model.{AirportDbo, CountryDbo, RouteDbo}
import com.cmartin.aviation.repository.TestData._
import com.cmartin.aviation.repository.{AirportRepository, Common, CountryRepository, RouteRepository}
import org.scalatest.Inside._
import zio.ZLayer.Debug
import zio.{ZIO, ZLayer}

import java.sql.SQLIntegrityConstraintViolationException

class SlickRouteRepositorySpec
    extends SlickBaseRepositorySpec {

  val env =
    ZLayer.make[CountryRepository with AirportRepository with RouteRepository](
      Common.dbLayer,
      SlickCountryRepository.layer,
      SlickAirportRepository.layer,
      SlickRouteRepository.layer,
      Debug.mermaid
    )

  behavior of "SlickRouteRepository"

  "Insert" should "insert a Route into the repository" in {
    val program = for {
      coords                   <- insertOriginDestinationAirports(spainDbo, madDbo, bcnDbo)
      (originId, destinationId) = coords
      id                       <- RouteRepository.insert(RouteDbo(madBcnDistance, originId, destinationId))
    } yield id

    val id = unsafeRun(
      program.provideLayer(env)
    )

    id should be > 0L
  }

  it should "insert a sequence of Routes into the repository" in {

    val program = for {
      coords                   <- insertOriginDestinationAirports(spainDbo, madDbo, bcnDbo)
      (originId, destinationId) = coords
      ids                      <- RouteRepository.insert(
                                    Seq(RouteDbo(madBcnDistance, originId, destinationId), RouteDbo(madBcnDistance, destinationId, originId))
                                  )
    } yield ids

    val ids = unsafeRun(
      program.provideLayer(env)
    )

    assert(ids.forall(_ > 0L), "non positive entity identifier")
  }

  it should "fail to insert a duplicate Route into the repository" in {
    val program = for {
      coords                   <- insertOriginDestinationAirports(spainDbo, madDbo, bcnDbo)
      (originId, destinationId) = coords
      _                        <- RouteRepository.insert(RouteDbo(madBcnDistance, originId, destinationId))
      _                        <- RouteRepository.insert(RouteDbo(madBcnDistance, originId, destinationId))
    } yield ()

    val resultEither = unsafeRun(
      program.provideLayer(env).either
    )

    resultEither.left.value shouldBe a[SQLIntegrityConstraintViolationException]
  }

  it should "fail to insert a Route with missing origin airport into the repository" in {
    val program = for {
      countryId     <- CountryRepository.insert(spainDbo)
      destinationId <- AirportRepository.insert(bcnDbo.copy(countryId = countryId))
      _             <- RouteRepository.insert(RouteDbo(madBcnDistance, 0L, destinationId))
    } yield ()

    val resultEither = unsafeRun(
      program.provideLayer(env).either
    )

    resultEither.left.value shouldBe a[SQLIntegrityConstraintViolationException]
  }

  it should "fail to insert a Route with missing destination airport into the repository" in {
    val program = for {
      countryId <- CountryRepository.insert(spainDbo)
      originId  <- AirportRepository.insert(bcnDbo.copy(countryId = countryId))
      _         <- RouteRepository.insert(RouteDbo(madBcnDistance, originId, 0L))
    } yield ()

    val resultEither = unsafeRun(
      program.provideLayer(env).either
    )

    resultEither.left.value shouldBe a[SQLIntegrityConstraintViolationException]
  }

  "Update" should "update a Route retrieved from the repository" in {
    val updatedDistance = 1.23
    val program         = for {
      coords                   <- insertOriginDestinationAirports(spainDbo, madDbo, bcnDbo)
      (originId, destinationId) = coords
      id                       <- RouteRepository.insert(RouteDbo(madBcnDistance, originId, destinationId))
      dbo                      <- RouteRepository.findByOriginAndDestination(madIataCode, bcnIataCode)
      count                    <- RouteRepository.update(dbo.get.copy(distance = updatedDistance))
      updated                  <- RouteRepository.findByOriginAndDestination(madIataCode, bcnIataCode)
    } yield (updated, count)

    val (updated, count) = unsafeRun(
      program.provideLayer(env)
    )

    count shouldBe 1
    updated.isDefined shouldBe true
    updated.get.distance shouldBe updatedDistance
  }

  "Delete" should "delete a Route from the repository" in {
    val program = for {
      coords                   <- insertOriginDestinationAirports(spainDbo, madDbo, bcnDbo)
      (originId, destinationId) = coords
      id                       <- RouteRepository.insert(RouteDbo(madBcnDistance, originId, destinationId))
      count                    <- RouteRepository.deleteByOriginAndDestination(madIataCode, bcnIataCode)
    } yield count

    val count = unsafeRun(
      program.provideLayer(env)
    )

    count shouldBe 1
  }

  "Find" should "retrieve a Route sequence by origin airport" in {
    val program = for {
      countryId <- CountryRepository.insert(spainDbo)
      madId     <- AirportRepository.insert(madDbo.copy(countryId = countryId))
      bcnId     <- AirportRepository.insert(bcnDbo.copy(countryId = countryId))
      tfnId     <- AirportRepository.insert(tfnDbo.copy(countryId = countryId))
      _         <- insertRoundTripRoute(madBcnDistance, madId, bcnId)
      _         <- insertRoundTripRoute(madTfnDistance, madId, tfnId)
      _         <- insertRoundTripRoute(bcnTfnDistance, bcnId, tfnId)
      routes    <- RouteRepository.findByIataOrigin(madIataCode)
    } yield (routes, madId)

    val (routes, madId) = unsafeRun(
      program.provideLayer(env)
    )

    routes.map { route =>
      inside(route) {
        case RouteDbo(_, originId, _, _) =>
          originId shouldBe madId

      }
    }
    routes should have size 2
  }

  it should "retrieve a Route sequence by destination airport" in {
    val program = for {
      countryId <- CountryRepository.insert(spainDbo)
      madId     <- AirportRepository.insert(madDbo.copy(countryId = countryId))
      bcnId     <- AirportRepository.insert(bcnDbo.copy(countryId = countryId))
      tfnId     <- AirportRepository.insert(tfnDbo.copy(countryId = countryId))
      _         <- insertRoundTripRoute(madBcnDistance, madId, bcnId)
      _         <- insertRoundTripRoute(madTfnDistance, madId, tfnId)
      _         <- insertRoundTripRoute(bcnTfnDistance, bcnId, tfnId)
      routes    <- RouteRepository.findByIataDestination(bcnIataCode)
    } yield (routes, bcnId)

    val (routes, bcnId) = unsafeRun(
      program.provideLayer(env)
    )

    routes.map { route =>
      inside(route) {
        case RouteDbo(_, _, destinationId, _) =>
          destinationId shouldBe bcnId

      }
    }
    routes should have size 2
  }

  private def insertOriginDestinationAirports(
      country: CountryDbo,
      origin: AirportDbo,
      destination: AirportDbo
  ): ZIO[CountryRepository with AirportRepository, Throwable, (Long, Long)] = {
    for {
      countryId     <- CountryRepository.insert(country)
      originId      <- AirportRepository.insert(origin.copy(countryId = countryId))
      destinationId <- AirportRepository.insert(destination.copy(countryId = countryId))
    } yield (originId, destinationId)
  }

  private def insertRoundTripRoute(distance: Double, origin: Long, destination: Long) =
    for {
      _ <- RouteRepository.insert(RouteDbo(distance, origin, destination))
      _ <- RouteRepository.insert(RouteDbo(distance, destination, origin))
    } yield ()

}
