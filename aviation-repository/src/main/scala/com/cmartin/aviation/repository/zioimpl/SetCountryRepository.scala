package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.CountryRepository
import com.cmartin.aviation.repository.Model.CountryDbo
import com.cmartin.aviation.repository.zioimpl.CommonAbstractions.Repository.BaseRepository
import zio.stm.{STM, TRef, TSet}
import zio.{Task, ZLayer}

final case class SetCountryRepository(seq: TRef[Int], set: TSet[CountryDbo])
    extends BaseRepository[CountryDbo]
    with CountryRepository {

  override def find(id: Long): Task[Option[CountryDbo]] =
    STM.atomically {
      for {
        countries <- set.toSet
        country   <- STM.succeed(countries.find(_.id == id))
      } yield country
    }

  override def insert(e: CountryDbo): Task[Long] =
    STM.atomically {
      for {
        id <- seq.updateAndGet(s => s + 1)
        _  <- set.put(e.copy(id = id))
      } yield id.toLong
    }

  override def insertSeq(seq: Seq[CountryDbo]): Task[Seq[Long]] = ???

  override def update(e: CountryDbo): Task[Int] =
    STM.atomically {
      for {
        countries <- set.toSet
        opt       <- STM.succeed(countries.find(_.code == e.code))
        count     <- opt.fold(
                       STM.succeed(0)
                     )(c => set.transform(old => e.copy(id = old.id)).map(_ => 1))
      } yield count
    }

  override def count(): Task[Int] =
    for {
      size <- set.size.commit
    } yield size

  override def findByCode(code: String): Task[Option[CountryDbo]] =
    STM.atomically {
      for {
        countries <- set.toSet
        a         <- STM.succeed(countries.find(_.code == code))
      } yield a
    }

  override def delete(code: String): Task[Int] =
    STM.atomically {
      for {
        countries <- set.toSet
        opt       <- STM.succeed(countries.find(_.code == code))
        count     <- deleteCountry(opt)
      } yield count
    }

  override def delete(id: Long): Task[Int] =
    STM.atomically {
      for {
        countries <- set.toSet
        opt       <- STM.succeed(countries.find(_.id == id))
        count     <- deleteCountry(opt)
      } yield count
    }

  private def deleteCountry(option: Option[CountryDbo]) =
    option.fold(
      STM.succeed(0)
    )(a => set.delete(a).map(_ => 1))

}

object SetCountryRepository {
  // new/constructor like
  val layer =
    ZLayer.fromFunction((seq, db) => SetCountryRepository(seq, db))

}
