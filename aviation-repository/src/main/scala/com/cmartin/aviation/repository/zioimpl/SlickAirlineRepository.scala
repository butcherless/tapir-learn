package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.AirlineRepository
import com.cmartin.aviation.repository.Model.AirlineDbo
import com.cmartin.aviation.repository.zioimpl.CommonAbstractions.Repository.AbstractLongRepository
import com.cmartin.aviation.repository.zioimpl.Tables.Airlines
import slick.jdbc.{JdbcBackend, JdbcProfile}
import zio.{RLayer, Task, ZLayer}

case class SlickAirlineRepository(db: JdbcBackend#DatabaseDef)
    extends AbstractLongRepository[AirlineDbo, Airlines](db)
    with JdbcProfile
    with AirlineRepository {

  import api._
  import common.Implicits._

  override val entities = Tables.airlines

  override def deleteByIataCode(iataCode: String): Task[Int] = {
    val query = entities.filter(_.iataCode === iataCode)
    query.delete
      .toZio
      .provideDbLayer(db)
  }

  override def findByIataCode(code: String): Task[Option[AirlineDbo]] = {
    val query = entities.filter(_.iataCode === code)
    query.result.headOption
      .toZio
      .provideDbLayer(db)
  }

  override def findByIcaoCode(code: String): Task[Option[AirlineDbo]] = {
    val query = entities.filter(_.icaoCode === code)
    query.result.headOption
      .toZio
      .provideDbLayer(db)
  }

  override def findByCountryCode(code: String): Task[Seq[AirlineDbo]] = {
    val query = for {
      airline <- entities
      country <- airline.country if country.code === code
    } yield airline

    query.result
      .toZio
      .provideDbLayer(db)
  }

  override def findByName(name: String): Task[Seq[AirlineDbo]] = {
    val query = entities.filter(_.name like s"%$name%")
    query.result
      .toZio
      .provideDbLayer(db)
  }

}

object SlickAirlineRepository {
  val layer: RLayer[JdbcBackend#DatabaseDef, AirlineRepository] =
    ZLayer.fromFunction(SlickAirlineRepository(_))
}
