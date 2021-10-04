package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.AirlineRepository
import com.cmartin.aviation.repository.Model.AirlineDbo
import com.cmartin.aviation.repository.zioimpl.Abstractions.AbstractLongRepository
import com.cmartin.aviation.repository.zioimpl.Tables.Airlines
import com.cmartin.aviation.repository.zioimpl.common.Dbio2Zio
import slick.interop.zio.DatabaseProvider
import slick.jdbc.JdbcProfile
import zio.Has
import zio.IO
import zio.ZLayer

class AirlineRepositoryLive(db: DatabaseProvider, profile: JdbcProfile)
    extends AbstractLongRepository[AirlineDbo, Airlines](db, profile)
    with AirlineRepository {

  import profile.api._

  override val entities = Tables.airlines

  override def deleteByIataCode(iataCode: String): IO[Throwable, Int] = {
    val query = entities.filter(_.iataCode === iataCode)
    (query.delete, db).toZio
  }

  override def findByIataCode(code: String): IO[Throwable, Option[AirlineDbo]] = {
    val query = entities.filter(_.iataCode === code)
    (query.result.headOption, db).toZio
  }

  override def findByIcaoCode(code: String): IO[Throwable, Option[AirlineDbo]] = {
    val query = entities.filter(_.icaoCode === code)
    (query.result.headOption, db).toZio
  }

  override def findByCountryCode(code: String): IO[Throwable, Seq[AirlineDbo]] = {
    val query = for {
      airline <- entities
      country <- airline.country if country.code === code
    } yield airline

    (query.result, db).toZio
  }

  override def findByName(name: String): IO[Throwable, Seq[AirlineDbo]] = {
    val query = entities.filter(_.name like s"%$name%")
    (query.result, db).toZio
  }

}

object AirlineRepositoryLive {

  val layer: ZLayer[Has[DatabaseProvider], Throwable, Has[AirlineRepository]] = {
    ZLayer.fromServiceM { provider =>
      provider.profile.map { profile =>
        new AirlineRepositoryLive(provider, profile)
      }
    }
  }

}
