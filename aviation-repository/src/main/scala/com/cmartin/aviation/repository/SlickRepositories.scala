package com.cmartin.aviation.repository

import com.cmartin.aviation.domain.Model._
import com.cmartin.aviation.port.CountryRepository
import com.cmartin.aviation.repository.Model.CountryDbo
import com.cmartin.aviation.repository.SlickInfrastructure.{Repositories, SlickCountryRepository}
import slick.interop.zio.DatabaseProvider
import zio.{Has, IO, ZLayer}

object SlickRepositories {
  class CountryRepositoryImpl extends CountryRepository {

    override def create(country: Country): IO[ServiceError, Long] = {
      for {
        dbo <- IO.succeed(CountryDbo(country.name, country.code))
      } yield 0L
    }

    override def findByCode(code: String): IO[ServiceError, Option[Country]] = ???
  }
}
