package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.CountryRepository
import com.cmartin.aviation.repository.Model
import com.cmartin.aviation.repository.TestData._
import zio.IO

import java.sql.SQLTimeoutException

object TestRepositories {
  val countryRepository = new CountryRepository {

    override def insert(e: Model.CountryDbo): IO[Throwable, Long] = ???

    override def insert(seq: Seq[Model.CountryDbo]): IO[Throwable, Seq[Long]] = ???

    override def update(e: Model.CountryDbo): IO[Throwable, Int] = ???

    override def count(): IO[Throwable, Int] = ???

    override def findByCode(code: String): IO[Throwable, Option[Model.CountryDbo]] = code match {
      //case "es" =>        IO.some(Model.CountryDbo(spainText, spainCode, Some(1L)))
      case _ =>
        IO.fail(new SQLTimeoutException("statement timeout reached"))
    }

    override def delete(code: String): IO[Throwable, Int] =
      IO.fail(new SQLTimeoutException("statement timeout reached"))

  }
}
