package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Model.CountryDbo
import com.cmartin.aviation.repository.zioimpl.Abstractions.AbstractLongRepository
import com.cmartin.aviation.repository.zioimpl.Tables.Countries
import com.cmartin.aviation.repository.zioimpl.common.Dbio2Zio
import slick.interop.zio.DatabaseProvider
import slick.jdbc.JdbcProfile
import zio.Has
import zio.IO
import zio.ZIO
import zio.ZLayer

object SlickCountryRepository {

  class CountryRepositoryImpl(db: DatabaseProvider, profile: JdbcProfile)
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

  val live: ZLayer[Has[DatabaseProvider], Throwable, Has[CountryRepository]] = {
    ZLayer.fromServiceM { provider =>
      provider.profile.map { profile =>
        new CountryRepositoryImpl(provider, profile)
      }
    }
  }

  def findByCode(code: String): ZIO[Has[CountryRepository], Throwable, Option[CountryDbo]] =
    ZIO.accessM[Has[CountryRepository]](_.get.findByCode(code))

  def insert(dbo: CountryDbo): ZIO[Has[CountryRepository], Throwable, Long] =
    ZIO.accessM[Has[CountryRepository]](_.get.insert(dbo))

  def insert(seq: Seq[CountryDbo]): ZIO[Has[CountryRepository], Throwable, Seq[Long]] =
    ZIO.accessM[Has[CountryRepository]](_.get.insert(seq))

  def update(dbo: CountryDbo): ZIO[Has[CountryRepository], Throwable, Int] =
    ZIO.accessM[Has[CountryRepository]](_.get.update(dbo))

  def delete(code: String): ZIO[Has[CountryRepository], Throwable, Int] =
    ZIO.accessM[Has[CountryRepository]](_.get.delete(code))

  def count(): ZIO[Has[CountryRepository], Throwable, Int] =
    ZIO.accessM[Has[CountryRepository]](_.get.count())
}
