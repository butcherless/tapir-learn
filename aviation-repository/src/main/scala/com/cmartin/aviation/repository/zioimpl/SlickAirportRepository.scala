package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.AirportRepository
import com.cmartin.aviation.repository.Model.AirportDbo
import com.cmartin.aviation.repository.zioimpl.CommonAbstractions.Repository.AbstractLongRepository
import com.cmartin.aviation.repository.zioimpl.Tables.Airports
import slick.jdbc.{JdbcBackend, JdbcProfile}
import zio.{IO, RLayer, ZLayer}

case class SlickAirportRepository(db: JdbcBackend#DatabaseDef)
    extends AbstractLongRepository[AirportDbo, Airports](db)
    with JdbcProfile
    with AirportRepository {

  import api._
  import common.Implicits._

  override val entities = Tables.airports

  override def deleteByIataCode(iataCode: String): IO[Throwable, Int] = {
    val query = entities.filter(_.iataCode === iataCode)
    query.delete
      .toZio
      .provideDbLayer(db)
  }

  override def findByIataCode(code: String): IO[Throwable, Option[AirportDbo]] = {
    val query = entities.filter(_.iataCode === code)
    query.result.headOption
      .toZio
      .provideDbLayer(db)
  }

  override def findByIcaoCode(code: String): IO[Throwable, Option[AirportDbo]] = {
    val query = entities.filter(_.icaoCode === code)
    query.result.headOption
      .toZio
      .provideDbLayer(db)
  }

  override def findByCountryCode(code: String): IO[Throwable, Seq[AirportDbo]] = {
    val query = for {
      airport <- entities
      country <- airport.country if country.code === code
    } yield airport

    query.result
      .toZio
      .provideDbLayer(db)
  }

  override def findByName(name: String): IO[Throwable, Seq[AirportDbo]] = {
    val query = entities.filter(_.name like s"%$name%")
    query.result
      .toZio
      .provideDbLayer(db)
  }

}

object SlickAirportRepository {
  val layer: RLayer[JdbcBackend#DatabaseDef, AirportRepository] =
    ZLayer.fromFunction(SlickAirportRepository(_))
}
