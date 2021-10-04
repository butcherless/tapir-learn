package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.CountryRepository
import com.cmartin.aviation.repository.Model.CountryDbo
import com.cmartin.aviation.repository.zioimpl.Abstractions.AbstractLongRepository
import com.cmartin.aviation.repository.zioimpl.Tables.Countries
import com.cmartin.aviation.repository.zioimpl.common.Dbio2Zio
import slick.interop.zio.DatabaseProvider
import slick.jdbc.JdbcProfile
import zio.Has
import zio.IO
import zio.ZLayer

class CountryRepositoryLive(db: DatabaseProvider, profile: JdbcProfile)
    extends AbstractLongRepository[CountryDbo, Countries](db, profile)
    with CountryRepository {

  import profile.api._

  override val entities = Tables.countries

  override def findByCode(code: String): IO[Throwable, Option[CountryDbo]] = {
    val query = entities.filter(_.code === code)
    (query.result.headOption, db).toZio
  }

  override def delete(code: String): IO[Throwable, Int] = {
    val query = entities.filter(_.code === code)
    (query.delete, db).toZio
  }

}

object CountryRepositoryLive {
  val layer: ZLayer[Has[DatabaseProvider], Throwable, Has[CountryRepository]] = {
    ZLayer.fromServiceM { provider =>
      provider.profile.map { profile =>
        new CountryRepositoryLive(provider, profile)
      }
    }
  }
}
