package com.cmartin.aviation.repository

import com.cmartin.aviation.domain.Model._
import com.cmartin.aviation.port.CountryPersister
import com.cmartin.aviation.repository.Model.CountryDbo
import zio.{Function1ToLayerSyntax, Has, IO, UIO, URLayer}
import zio.logging.{Logging, log}

object SlickRepositories {

  //TODO remove
  class CountryRepositoryImpl extends CountryPersister {

    override def update(country: Country): IO[ServiceError, Int] = ???

    override def delete(code: CountryCode): IO[ServiceError, Int] = ???

    override def existsByCode(code: CountryCode): IO[ServiceError, Boolean] = ???

    override def insert(country: Country): IO[ServiceError, Long] = {
      for {
        dbo <- IO.succeed(CountryDbo(country.name, country.code))
      } yield 0L
    }

    override def findByCode(code: CountryCode): IO[ServiceError, Option[Country]] = ???
  }

  case class CountryRepositoryLive(logging: Logging)
      extends CountryPersister {

    override def insert(country: Country): IO[ServiceError, Long] =
      (
        for {
          _ <- log.debug(s"insert: $country")
          id <- IO.succeed(1L) // TODO slick impl
        } yield id
      ).provide(logging)

    override def existsByCode(code: CountryCode): IO[ServiceError, Boolean] =
      (
        for {
          _ <- log.debug(s"existsByCode: $code")
          exists <- UIO.succeed(true) // TODO slick impl
        } yield exists
      ).provide(logging)

    override def findByCode(code: CountryCode): IO[ServiceError, Option[Country]] =
      (
        for {
          _ <- log.debug(s"findByCode: $code")
        } yield Some(Country(code, s"Country-name-for-$code")) //TODO slick impl
      ).provide(logging)

    override def update(country: Country): IO[ServiceError, Int] = ???

    override def delete(code: CountryCode): IO[ServiceError, Int] = ???

  }

  object CountryRepositoryLive {
    val layer: URLayer[Has[Logging], Has[CountryPersister]] =
      (CountryRepositoryLive(_)).toLayer
  }

}
