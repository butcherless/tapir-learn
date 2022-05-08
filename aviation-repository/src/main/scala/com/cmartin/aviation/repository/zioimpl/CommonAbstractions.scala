package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Model.LongDbo
import slick.jdbc.JdbcBackend
import slick.jdbc.JdbcProfile
import zio.Task

//TODO refactor: move to common module/project
object CommonAbstractions extends JdbcProfile {
  import api._
  import common.Implicits.Dbio2Zio

  object Table {
    abstract class LongBasedTable[T <: LongDbo](tag: Tag, tableName: String)
        extends Table[T](tag, tableName) {
      /* primary key column */
      def id: Rep[Long] = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    }
  }

  object Repository {
    import Table.LongBasedTable

    trait BaseRepository[E <: LongDbo] {
      def find(id: Long): Task[Option[E]]
      def insert(e: E): Task[Long]
      def insertSeq(seq: Seq[E]): Task[Seq[Long]]
      def update(e: E): Task[Int]
      def count(): Task[Int]
    }

    abstract class AbstractLongRepository[E <: LongDbo, T <: LongBasedTable[E]](
        db: JdbcBackend#DatabaseDef
    ) extends BaseRepository[E] {

      val entities: TableQuery[T]

      override def find(id: Long): Task[Option[E]] = {
        val query = entities.filter(_.id === id)
        query.result.headOption
          .toZio()
          .provideService(db)
      }
      override def count(): Task[Int] =
        entities.length.result
          .toZio()
          .provideService(db)

      override def update(e: E): Task[Int] = {
        val query = entities.filter(_.id === e.id)
        query.update(e)
          .toZio()
          .provideService(db)
      }

      override def insert(e: E): Task[Long] = {
        val action = (entities returning entities.map(_.id)) += e
        action
          .toZio()
          .provideService(db)
      }

      override def insertSeq(seq: Seq[E]): Task[Seq[Long]] = {
        val action = entities returning entities.map(_.id) ++= seq
        action
          .toZio()
          .provideService(db)
      }

    }
  }

}
