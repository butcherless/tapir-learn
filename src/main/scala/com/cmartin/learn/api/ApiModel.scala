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

  def buildInfo(): BuildInfo =
    BuildInfo(
      APP_NAME,
      LocalDateTime.now(Clock.systemDefaultZone()).toString,
      APP_VERSION,
      Success
    )
}
