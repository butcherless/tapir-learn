package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.BaseRepository
import com.cmartin.aviation.repository.Model.LongDbo
import com.cmartin.aviation.repository.zioimpl.Tables.LongBasedTable
import com.cmartin.aviation.repository.zioimpl.common.Dbio2Zio
import slick.interop.zio.DatabaseProvider
import slick.jdbc.JdbcProfile
import zio.Has
import zio.Task

object Abstractions {

  abstract class AbstractLongRepository[E <: LongDbo, T <: LongBasedTable[E]](
      db: DatabaseProvider,
      val profile: JdbcProfile
  ) extends BaseRepository[E] {

    import profile.api._

    val entities: TableQuery[T]

    override def find(id: Long): Task[Option[E]] = {
      val query = entities.filter(_.id === id)
      query.result.headOption
        .toZio
        .provide(Has(db))
    }
    override def count(): Task[Int] =
      entities.length.result
        .toZio
        .provide(Has(db))

    override def update(e: E): Task[Int] = {
      val query = entities.filter(_.id === e.id)
      query.update(e)
        .toZio
        .provide(Has(db))
    }

    override def insert(e: E): Task[Long] = {
      val action = (entities returning entities.map(_.id)) += e
      action
        .toZio
        .provide(Has(db))
    }

    override def insertSeq(seq: Seq[E]): Task[Seq[Long]] = {
      val action = entities returning entities.map(_.id) ++= seq
      action
        .toZio
        .provide(Has(db))
    }

  }
}
