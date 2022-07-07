package com.cmartin.aviation

import zio.stm.{STM, TMap, USTM}
import zio.{Runtime, _}
//import zio.logging.slf4j.Slf4jLogger

import com.cmartin.aviation.domain.Model._

object Commons {

  // type ServiceResponseOld[A] = ZIO[Logging, ServiceError, A]
  type ServiceResponse[A] = IO[ServiceError, A]
  // type RepositoryResponse[A] = ZIO[Logging, RepositoryError, A]

  val runtime =
    Runtime.default

  // val loggingEnv: ZLayer[Any, Nothing, Has[Logging]] = ???
  // Slf4jLogger.make((_, message) => message)      .map(Has(_))

  val memDB: USTM[TMap[String, String]] = TMap.empty[String, String]

  // [C]rud
  def addPair(key: String, value: String): UIO[Unit]              =
    STM.atomically(
      for {
        map    <- memDB
        result <- map.put(key, value)
      } yield result
    )
  // c[R]ud
  def getValue(key: String): UIO[Option[String]]                  =
    STM.atomically(
      for {
        map    <- memDB
        result <- map.get(key)
      } yield result
    )
  // cr[U]d
  def updatePair(key: String, value: String): UIO[Option[String]] =
    STM.atomically(
      for {
        map    <- memDB
        result <- map.updateWith(key)(_ => Some(value))
      } yield result
    )
  // cru[D]
  def deleteKey(key: String): UIO[Unit]                           =
    STM.atomically(
      for {
        map    <- memDB
        result <- map.delete(key)
      } yield result
    )

}
