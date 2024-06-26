package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Model.LongDbo
import slick.jdbc.{JdbcBackend, JdbcProfile}
import zio.Task

//TODO refactor: move to common module/project
object CommonAbstractions
    extends JdbcProfile {
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
    import common.Implicits.QueryToLayer

    trait BaseRepository[E <: LongDbo] {

      /** Retrieve the entity option by its id
        *
        * @param id
        *   identifier for the entity to found
        * @return
        *   Some(e) or None
        */
      def find(id: Long): Task[Option[E]]

      /** Inserts the entity returning the generated identifier
        *
        * @param e
        *   entity to be added
        * @return
        *   entity id after the insert
        */
      def insert(e: E): Task[Long]

      /** Inserts a sequence of entities returning the generated sequence of
        * identifiers
        *
        * @param seq
        *   entity sequence
        * @return
        *   generated identifier sequence after the insert
        */
      def insertSeq(seq: Seq[E]): Task[Seq[Long]]

      /** Updates the entity in the repository
        *
        * @param e
        *   entity to be updated
        * @return
        *   number of entities affected
        */
      def update(e: E): Task[Int]

      /** Deletes the entity with the identifier supplied
        *
        * @param id
        *   entity identifier
        * @return
        *   number of entities affected
        */
      def delete(id: Long): Task[Int]

      /** Retrieve the repository entity count
        *
        * @return
        *   number of entities in the repo
        */
      def count(): Task[Int]
    }

    abstract class AbstractLongRepository[E <: LongDbo, T <: LongBasedTable[E]](
        db: JdbcBackend#DatabaseDef
    ) extends BaseRepository[E] {

      val entities: TableQuery[T]

      /*       implicit def provide[T](zio: RIO[JdbcBackend#DatabaseDef, T]): Task[T] =
        zio.provide(ZLayer.succeed(db))
       */

      override def find(id: Long): Task[Option[E]] = {
        val query = entities.filter(_.id === id)
        query.result.headOption
          .toZio
          .provideDbLayer(db)
      }

      override def count(): Task[Int] =
        entities.length.result
          .toZio
          .provideDbLayer(db)

      override def update(e: E): Task[Int] = {
        val query = entities.filter(_.id === e.id)
        query.update(e)
          .toZio
          .provideDbLayer(db)
      }

      override def insert(e: E): Task[Long] = {
        val action = (entities returning entities.map(_.id)) += e
        action.toZio
          .provideDbLayer(db)
      }

      override def insertSeq(seq: Seq[E]): Task[Seq[Long]] = {
        val action = entities returning entities.map(_.id) ++= seq
        action.toZio
          .provideDbLayer(db)
      }

      override def delete(id: Long): Task[Int] = {
        val query = entities.filter(_.id === id)
        query.delete
          .toZio
          .provideDbLayer(db)
      }

    }
  }

}
