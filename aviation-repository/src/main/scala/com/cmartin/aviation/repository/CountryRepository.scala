package com.cmartin.aviation.repository

import com.cmartin.aviation.repository.Model.CountryDbo
import zio.Has
import zio.RIO
import zio.Task
import zio.ZIO

trait CountryRepository
    extends BaseRepository[CountryDbo] {

  def findByCode(code: String): Task[Option[CountryDbo]]
  def delete(code: String): Task[Int]

}

object CountryRepository {
  def findByCode(code: String): RIO[Has[CountryRepository], Option[CountryDbo]] =
    ZIO.accessM[Has[CountryRepository]](_.get.findByCode(code))

  def insert(dbo: CountryDbo): RIO[Has[CountryRepository], Long] =
    ZIO.accessM[Has[CountryRepository]](_.get.insert(dbo))

  def insert(seq: Seq[CountryDbo]): RIO[Has[CountryRepository], Seq[Long]] =
    ZIO.accessM[Has[CountryRepository]](_.get.insert(seq))

  def update(dbo: CountryDbo): RIO[Has[CountryRepository], Int] =
    ZIO.accessM[Has[CountryRepository]](_.get.update(dbo))

  def delete(code: String): RIO[Has[CountryRepository], Int] =
    ZIO.accessM[Has[CountryRepository]](_.get.delete(code))

  def count(): RIO[Has[CountryRepository], Int] =
    ZIO.accessM[Has[CountryRepository]](_.get.count())

}
