package com.cmartin.aviation.repository

import Model._

object AbstractRepositories {

  trait AbstractRepository[F[_], E] {

    /** Retrieve all the entities in the repository
      *
      * @return the entity sequence
      */
    def findAll(): F[Seq[E]]

    /** Retrieve the entity option by its id
      *
      * @param id identifier for the entity to found
      * @return Some(e) or None
      */
    def findById(id: Option[Long]): F[Option[E]]

    /** Retrieve the repository entity count
      *
      * @return number of entities in the repo
      */
    def count(): F[Int]

    /** Inserts the entity returning the generated identifier
      *
      * @param e entity to be added
      * @return entity id after the insert
      */
    def insert(e: E): F[Long]

    /** Inserts a sequence of entities returning the generated sequence of identifiers
      *
      * @param seq entity sequence
      * @return generated identifier sequence after the insert
      */
    def insert(seq: Seq[E]): F[Seq[Long]]

    /** Updates the entity in the repository
      *
      * @param e entity to be updated
      * @return number of entities affected
      */
    def update(e: E): F[Int]

    /** Deletes the entity with the identifier supplied
      *
      * @param id entity identifier
      * @return number of entites affected
      */
    def delete(id: Long): F[Int]

    /** Deletes all the entities from the repository
      *
      * @return number of entites affected
      */
    def deleteAll(): F[Int]
  }

  trait AbstractCountryRepository[F[_]] extends AbstractRepository[F, CountryDbo] {
    def findByCode(code: String): F[Option[CountryDbo]]
  }

  trait AbstractAirportRepository[F[_]] extends AbstractRepository[F, AirportDbo] {
    def findByIataCode(code: String): F[Option[AirportDbo]]
    def findByIcaoCode(code: String): F[Option[AirportDbo]]
    def findByCountryCode(code: String): F[Seq[AirportDbo]]
    def findByName(name: String): F[Seq[AirportDbo]]

  }

  trait AbstractAirlineRepository[F[_]] extends AbstractRepository[F, AirlineDbo]

  trait AbstractRouteRepository[F[_]] extends AbstractRepository[F, RouteDbo] {
    def findByIataOrigin(iataCode: String): F[Seq[RouteDbo]]
    def findByIataDestination(iataCode: String): F[Seq[RouteDbo]]
  }
}
