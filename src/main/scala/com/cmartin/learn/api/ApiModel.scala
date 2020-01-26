package com.cmartin.learn.api

object ApiModel {

  val APP_NAME = "tapir learn web application"
  val APP_VERSION = "1.0.0-SNAPSHOT"

  sealed trait Currency

  case object EUR extends Currency

  case object USD extends Currency

  case class Transfer(
                       sender: String,
                       receiver: String,
                       amount: Double,
                       currency: Currency,
                       desc: String,
                       id: Option[Long] = scala.None
                     )


  // NESTED ENTITIES
  case class ACEntity(
                       composedId: ComposedId,
                       sids: Sids,
                       output: Output
                     )

  case class ComposedId(tid: Long, aid: Long)

  case class Sids(source: Source, state: Option[State]) // TODO Option state

  sealed trait Sid {
    val id: Long
    val filter: String
    val comStrategy: ComStrategy
  }

  case class Source(id: Long, filter: String, comStrategy: ComStrategy, name: Output) extends Sid

  case class State(id: Long, filter: String, comStrategy: ComStrategy, name: Output, perStrategy: PerStrategy, processors: Processors) extends Sid

  sealed trait Output

  case object ComOut extends Output

  case object ShaOut extends Output

  sealed trait ComStrategy

  case object Append extends ComStrategy

  case object Merge extends ComStrategy

  case object None extends ComStrategy

  sealed trait PerStrategy

  case object OveStrategy extends PerStrategy

  case object ShaStrategy extends PerStrategy

  case class Processors(ins: Seq[String], exs: Seq[String], trs: Seq[String])


  // Actuator
  case class ApiBuildInfo(
                           appName: String,
                           date: String,
                           version: String,
                           result: String
                         )


  // AVIATION MODEL
  // ADT => String representation for Codec. Model ADT => String ADT representation
  case class ApiAircraft(
                          registration: String,
                          age: Int,
                          model: String,
                          id: Option[Long] = scala.None
                        )


}
