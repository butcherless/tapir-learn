package com.cmartin.learn.api

import java.time.{Clock, LocalDateTime}

object ApiModel {

  val APP_NAME = "tapir learn web application"
  val APP_VERSION = "1.0.0-SNAPSHOT"

  sealed trait Currency

  case class EUR(code: String) extends Currency

  case class USD(code: String) extends Currency

  val eur = EUR("EUR")
  val usd = USD("EUR")

  case class Transfer(sender: String, receiver: String, amount: Double, currency: Currency, desc: String)

  case class BuildInfo(appName: String, date: String, version: String, result: Result)

  sealed trait Result

  object Success extends Result

  object Warning extends Result

  object Error extends Result

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

  //case class ComOut(name: String) extends Output
  case object ComOut extends Output

  //case class ShaOut(name: String) extends Output
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
      eur,
      "Viaje a Tenerife"
    )

  val acEntityExample: ACEntity =
    ACEntity(
      ComposedId(11111111L, 22222222L),
      Sids(
        Source(1111L, "src-filter", ComOut), Some(State(2222L, "sta-filter", ComOut, ShaStrategy,
          Processors(Seq("i1", "i2"), Seq.empty[String], Seq("t1", "t2", "t3"))))),
      // ShaOut("sha-out")
      ComOut
    )

  def buildInfo(): BuildInfo =
    BuildInfo(
      APP_NAME,
      LocalDateTime.now(Clock.systemDefaultZone()).toString,
      APP_VERSION,
      Success
    )


}
