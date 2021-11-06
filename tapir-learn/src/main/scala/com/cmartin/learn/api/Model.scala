package com.cmartin.learn.api

import sttp.tapir._

import java.time.Instant
import java.time.LocalDateTime

object Model {

  type TransferId = Long

  val APP_NAME = "tapir learn web application"
  val APP_VERSION = "1.0.0-SNAPSHOT"

  trait EnumHelper { e: Enumeration =>
    import io.circe._

    implicit val enumDecoder: Decoder[e.Value] = Decoder.decodeEnumeration(e)
    implicit val enumEncoder: Encoder[e.Value] = Encoder.encodeEnumeration(e)

    // needs to be a def or lazy val so that the enumeration values are available!
    implicit def schemaForEnum: Schema[e.Value] =
      Schema.string.validate(Validator.enumeration(e.values.toList, v => Option(v)))
  }

  object AircraftType extends Enumeration with EnumHelper {
    type AircraftType = Value
    val AirbusA320, AirbusA320N, Airbus332, AirbusA333, Boeing737NG, Boeing788, Boeing789 = Value
  }

  sealed trait Sid {
    val id: Long
    val filter: String
    val comStrategy: ComStrategy
  }

  sealed trait Output

  sealed trait ComStrategy

  sealed trait PerStrategy

  sealed trait ErrorInfo {
    val code: String
    val message: String
  }

  case class TransferDto(
      sender: String,
      receiver: String,
      amount: Double,
      currency: String,
      date: Instant,
      desc: String
  )

  // NESTED ENTITIES
  case class ACEntity(
      composedId: ComposedId,
      sids: Sids,
      output: Output
  )

  case class ComposedId(tid: Long, aid: Long)

  case class Sids(source: Source, state: Option[State]) // TODO Option state

  case class Source(id: Long, filter: String, comStrategy: ComStrategy, name: Output) extends Sid

  case class State(
      id: Long,
      filter: String,
      comStrategy: ComStrategy,
      name: Output,
      perStrategy: PerStrategy,
      processors: Processors
  ) extends Sid

  case class Processors(ins: Seq[String], exs: Seq[String], trs: Seq[String])

  case class BuildInfoDto(
      name: String,
      version: String,
      scalaVersion: String,
      sbtVersion: String,
      gitCommit: String,
      builtAtMillis: LocalDateTime
  )

  case class ApiBuildInfo(
      appName: String,
      date: String,
      version: String,
      result: String
  )

  // AVIATION MODEL
  // ADT => String representation for Codec. Model ADT => String ADT representation
  import AircraftType._
  case class AircraftDto(
      registration: String,
      age: Int,
      model: AircraftType,
      id: Option[Long] = scala.None
  )

  case class BadRequestError(code: String, message: String) extends ErrorInfo

  case class NotFoundError(code: String, message: String) extends ErrorInfo

  case class ServerError(code: String, message: String) extends ErrorInfo

  // Actuator

  case class ServiceUnavailableError(code: String, message: String) extends ErrorInfo

  case class UnknownError(code: String, message: String) extends ErrorInfo

  case class ComOut() extends Output

  case class ShaOut() extends Output

  case object Append extends ComStrategy

  case object Merge extends ComStrategy

  case object None extends ComStrategy

  case object OveStrategy extends PerStrategy

  case object ShaStrategy extends PerStrategy

}
