package com.cmartin.aviation.repository

import com.cmartin.aviation.repository.Model.CountryDbo
import com.cmartin.aviation.repository.zioimpl.CommonAbstractions.Repository.BaseRepository
import zio.{RIO, Task, ZIO}

trait CountryRepository
    extends BaseRepository[CountryDbo] {

  /** Retrieves a Country by its code
    *
    * @param code
    *   country code
    * @return
    *   the country
    */
  def findByCode(code: String): Task[Option[CountryDbo]]

  /** Deletes a Country by its code
    *
    * @param code
    *   country code
    * @return
    *   delete count
    */
  def delete(code: String): Task[Int]

}

object CountryRepository {
  def find(id: Long): RIO[CountryRepository, Option[CountryDbo]] =
    ZIO.serviceWithZIO[CountryRepository](_.find(id))

  def findByCode(code: String): RIO[CountryRepository, Option[CountryDbo]] =
    ZIO.serviceWithZIO[CountryRepository](_.findByCode(code))

  def insert(dbo: CountryDbo): RIO[CountryRepository, Long] =
    ZIO.serviceWithZIO[CountryRepository](_.insert(dbo))

  def insert(seq: Seq[CountryDbo]): RIO[CountryRepository, Seq[Long]] =
    ZIO.serviceWithZIO[CountryRepository](_.insertSeq(seq))

  def update(dbo: CountryDbo): RIO[CountryRepository, Int] =
    ZIO.serviceWithZIO[CountryRepository](_.update(dbo))

  def delete(code: String): RIO[CountryRepository, Int] =
    ZIO.serviceWithZIO[CountryRepository](_.delete(code))

  def count(): RIO[CountryRepository, Int] =
    ZIO.serviceWithZIO[CountryRepository](_.count())

}
