package com.cmartin.aviation.repository

import com.cmartin.aviation.repository.AbstractRepositories.AbstractRepository
import slick.jdbc.JdbcProfile

object JdbcDefinitions {

  /** Relational Profile for mapping domain and persistence models
    */
  trait Profile {
    val profile: JdbcProfile
  }

  trait BaseDefinitions {
    self: JdbcProfile =>

    import Model._
    import api._

    abstract class OldLongBasedTable[T <: LongDbo](tag: Tag, tableName: String) extends Table[T](tag, tableName) {
      /* primary key column */
      def id: Rep[Long] = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    }

    /*
    abstract class ForeignLongTable[T <: LongDbo](tag: Tag, tableName: String) extends Table[T](tag, tableName) {
      /* primary key column */
      def id: Rep[Long] = column[Long]("ID")
    }
     */

    abstract class RelationBasedTable[T <: RelationDbo](tag: Tag, tableName: String) extends Table[T](tag, tableName) {
      /* primary key columns */
      def left: Rep[Long]  = column[Long]("LEFT")
      def right: Rep[Long] = column[Long]("RIGHT")
    }

    /*
    abstract class AbstractRelationRepository[E <: RelationDbo, T <: RelationBasedTable[E]] {
      val entities: TableQuery[T]

      private def pkFilter(t: T, id: LongTuple): Rep[Boolean] =
        t.left === id._1 && t.right === id._2

      def findById(id: LongTuple): DBIO[Option[E]] =
        entities.filter(t => pkFilter(t, id)).result.headOption

      def findAll(): DBIO[Seq[E]] =
        entities.result

      def count(): DBIO[Int] =
        entities.length.result

      def insert(e: E): DBIO[LongTuple] =
        entityReturningId() += e

      def insert(seq: Seq[E]): DBIO[Seq[LongTuple]] =
        entities returning entities.map(e => (e.left, e.right)) ++= seq

      def update(e: E): DBIO[Int] =
        entities.filter(t => pkFilter(t, e.id)).update(e)

      def delete(id: LongTuple): DBIO[Int] =
        entities.filter(t => pkFilter(t, id)).delete

      def deleteAll(): DBIO[Int] =
        entities.delete

      private def entityReturningId(): ReturningInsertActionComposer[E, LongTuple] =
        entities returning entities.map(e => (e.left, e.right))
    }
     */

    /*
    abstract class AbstractForeignLongRepository[E <: LongDbo, T <: ForeignLongTable[E]] {
      val entities: TableQuery[T]

      private lazy val entityReturningId = entities returning entities.map(_.id)

      def insert(e: E): DBIO[Long] = entityReturningId += e
    }
     */

    abstract class AbstractLongRepository[E <: LongDbo, T <: OldLongBasedTable[E]]
        extends AbstractRepository[DBIO, E] {
      val entities: TableQuery[T]

      def findById(id: Option[Long]): DBIO[Option[E]] = {
        entities.filter(_.id === id).result.headOption
      }

      def findAll(): DBIO[Seq[E]] =
        entities.result

      def count(): DBIO[Int] =
        entities.length.result

      def insert(e: E): DBIO[Long] =
        entityReturningId() += e

      def insert(seq: Seq[E]): DBIO[Seq[Long]] =
        entities returning entities.map(_.id) ++= seq

      def update(e: E): DBIO[Int] =
        entities.filter(_.id === e.id).update(e)

      def delete(id: Long): DBIO[Int] =
        entities.filter(_.id === id).delete

      def deleteAll(): DBIO[Int] =
        entities.delete

      private def entityReturningId(): ReturningInsertActionComposer[E, Long] =
        entities returning entities.map(_.id)
    }
  }
}
