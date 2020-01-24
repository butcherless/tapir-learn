package com.cmartin.learn.api

import java.time.{Clock, LocalDateTime}

object ApiModel {

  val APP_NAME = "tapir learn web application"
  val APP_VERSION = "1.0.0-SNAPSHOT"

  sealed trait Currency

  case object EUR extends Currency

  case object USD extends Currency

  case class Transfer(sender: String, receiver: String, amount: Double, currency: Currency, desc: String)


  sealed trait Result

  case object Success extends Result

  case object Warning extends Result

  case object Error extends Result

  case class BuildInfo(appName: String, date: String, version: String, result: Result)

  // NESTED ENTITIES
  case class ACEntity(
                       composedId: ComposedId,
                       sids: Sids,
                       output: Output
                     )

  case class ComposedId(tid: Long, aid: Long)

  case class Sids(source: Source, state: Option[State]) // TODO Option state

  sealed trait Sid {
    val filter: String
  }

  case class Source(id: Long, filter: String, name: Output) extends Sid

  case class State(id: Long, filter: String, name: Output, perStrategy: PerStrategy, procs: Processors) extends Sid

  sealed trait Output

  case object ComOut extends Output

  case object ShaOut extends Output

  sealed trait PerStrategy

  case object OveStrategy extends PerStrategy

  case object ShaStrategy extends PerStrategy

  case class Processors(ins: Seq[String], exs: Seq[String], trs: Seq[String])

  /*
    API Objects examples
   */

  val transferExample =
    Transfer(
      "ES11 0182 1111 2222 3333 4444",
      "ES99 2038 9999 8888 7777 6666",
      100.00,
      EUR,
      "Viaje a Tenerife"
    )


  def buildInfo(): BuildInfo =
    BuildInfo(
      APP_NAME,
      LocalDateTime.now(Clock.systemDefaultZone()).toString,
      APP_VERSION,
      Success
    )


}
