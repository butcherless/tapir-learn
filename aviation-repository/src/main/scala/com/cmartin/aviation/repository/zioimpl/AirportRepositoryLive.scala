package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.AirportRepository
import com.cmartin.aviation.repository.Model.AirportDbo
import com.cmartin.aviation.repository.zioimpl.Abstractions.AbstractLongRepository
import com.cmartin.aviation.repository.zioimpl.Tables.Airports
import com.cmartin.aviation.repository.zioimpl.common.Dbio2Zio
import slick.interop.zio.DatabaseProvider
import slick.jdbc.JdbcProfile
import zio.Has
import zio.IO
import zio.ZLayer

class AirportRepositoryLive(db: DatabaseProvider, profile: JdbcProfile)
    extends AbstractLongRepository[AirportDbo, Airports](db, profile)
    with AirportRepository {

  import profile.api._

  override val entities = Tables.airports

  override def deleteByIataCode(iataCode: String): IO[Throwable, Int] = {
    val query = entities.filter(_.iataCode === iataCode)
    (query.delete, db).toZio
  }

  override def findByIataCode(code: String): IO[Throwable, Option[AirportDbo]] = {
    val query = entities.filter(_.iataCode === code)
    (query.result.headOption, db).toZio
  }

  override def findByIcaoCode(code: String): IO[Throwable, Option[AirportDbo]] = {
    val query = entities.filter(_.icaoCode === code)
    (query.result.headOption, db).toZio
  }

  override def findByCountryCode(code: String): IO[Throwable, Seq[AirportDbo]] = {
    val query = for {
      airport <- entities
      country <- airport.country if country.code === code
    } yield airport

    (query.result, db).toZio
  }

  override def findByName(name: String): IO[Throwable, Seq[AirportDbo]] = {
    val query = entities.filter(_.name like s"%$name%")
    (query.result, db).toZio
  }

}

object AirportRepositoryLive {

  val layer: ZLayer[Has[DatabaseProvider], Throwable, Has[AirportRepository]] = {
    ZLayer.fromServiceM { provider =>
      provider.profile.map { profile =>
        new AirportRepositoryLive(provider, profile)
      }
    }
  }

}
