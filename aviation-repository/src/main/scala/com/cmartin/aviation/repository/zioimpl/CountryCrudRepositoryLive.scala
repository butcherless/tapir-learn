package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.domain.Model.Country
import com.cmartin.aviation.domain.Model.ServiceError
import com.cmartin.aviation.domain.Model.UnexpectedServiceError
import com.cmartin.aviation.port.CountryCrudRepository
import com.cmartin.aviation.repository.Model.CountryDbo
import zio._

case class CountryCrudRepositoryLive(contryRepository: CountryRepository)
    extends CountryCrudRepository {

  override def create(country: Country): IO[ServiceError, Long] = {
    val program = for {
      // log
      dbo <- IO.succeed(CountryDbo(country.name, country.code))
      id <- contryRepository.insert(dbo)
    } yield id

    //TODO manageError function
    program.mapError {
      case e @ _ => UnexpectedServiceError(e.getMessage())
    }
  }

  override def findByCode(code: String): IO[ServiceError, Option[Country]] = ???

}

object CountryCrudRepositoryLive {
  val layer: URLayer[Has[CountryRepository], Has[CountryCrudRepository]] =
    (CountryCrudRepositoryLive(_)).toLayer
}
