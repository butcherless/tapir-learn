package com.cmartin.aviation.repository

import com.cmartin.aviation.repository.Model.CountryDbo
import zio.{Has, IO, ZIO}

trait CountryRepository
    extends BaseRepository[CountryDbo] {

  def findByCode(code: String): IO[Throwable, Option[CountryDbo]]
  def delete(code: String): IO[Throwable, Int]

}

object CountryRepository {
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

