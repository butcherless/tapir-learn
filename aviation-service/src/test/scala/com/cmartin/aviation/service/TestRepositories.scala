package com.cmartin.aviation.service

import com.cmartin.aviation.repository.AirportRepository
import com.cmartin.aviation.repository.CountryRepository
import com.cmartin.aviation.repository.Model._
import com.cmartin.aviation.repository.TestData._
import zio.IO
import zio.Task

import java.sql.SQLTimeoutException

object TestRepositories {
  val countryRepository = new CountryRepository {

    override def find(id: Long): Task[Option[CountryDbo]] = ???

    override def insert(e: CountryDbo): IO[Throwable, Long] = ???

    override def insert(seq: Seq[CountryDbo]): IO[Throwable, Seq[Long]] = ???

    override def update(e: CountryDbo): IO[Throwable, Int] = ???

    override def count(): IO[Throwable, Int] = ???

    override def findByCode(code: String): IO[Throwable, Option[CountryDbo]] = code match {
      //case "es" =>        IO.some(CountryDbo(spainText, spainCode, Some(1L)))
      case _ =>
        IO.fail(new SQLTimeoutException("statement timeout reached"))
    }

    override def delete(code: String): IO[Throwable, Int] =
      IO.fail(new SQLTimeoutException("statement timeout reached"))
  }

  val airportRepository = new AirportRepository {

    override def find(id: Long): Task[Option[AirportDbo]] = ???

    override def insert(e: AirportDbo): IO[Throwable, Long] = ???

    override def insert(seq: Seq[AirportDbo]): IO[Throwable, Seq[Long]] = ???

    override def update(e: AirportDbo): IO[Throwable, Int] = ???

    override def count(): IO[Throwable, Int] = ???

    override def deleteByIataCode(code: String): IO[Throwable, Int] =
      IO.fail(new SQLTimeoutException("statement timeout reached"))

    override def findByIataCode(code: String): IO[Throwable, Option[AirportDbo]] =
      IO.fail(new SQLTimeoutException("statement timeout reached"))

    override def findByIcaoCode(code: String): IO[Throwable, Option[AirportDbo]] = ???

    override def findByCountryCode(code: String): IO[Throwable, Seq[AirportDbo]] = ???

    override def findByName(name: String): IO[Throwable, Seq[AirportDbo]] = ???

  }
}
