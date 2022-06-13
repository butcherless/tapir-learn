package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.CountryRepository
import com.cmartin.aviation.repository.Model.CountryDbo
import com.cmartin.aviation.repository.zioimpl.CommonAbstractions.Repository.AbstractLongRepository
import com.cmartin.aviation.repository.zioimpl.Tables.Countries
import slick.jdbc.{JdbcBackend, JdbcProfile}
import zio.{RLayer, Task, ZLayer}

case class SlickCountryRepository(db: JdbcBackend#DatabaseDef)
    extends AbstractLongRepository[CountryDbo, Countries](db)
    with JdbcProfile
    with CountryRepository {

  import api._
  import common.Implicits._

  override val entities = Tables.countries

  override def findByCode(code: String): Task[Option[CountryDbo]] = {
    val query = entities.filter(_.code === code)
    query.result.headOption
      .toZio()
      .provideDbLayer(db)
  }

  override def delete(code: String): Task[Int] = {
    val query = entities.filter(_.code === code)
    query.delete
      .toZio()
      .provideDbLayer(db)
  }

}

object SlickCountryRepository {
  val layer: RLayer[JdbcBackend#DatabaseDef, SlickCountryRepository] =
    ZLayer.fromFunction(SlickCountryRepository(_))
}
