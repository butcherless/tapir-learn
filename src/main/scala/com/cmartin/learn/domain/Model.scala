package com.cmartin.learn.domain

import java.time.Instant

object Model {

  sealed trait Currency

  // ADT
  sealed trait AircraftModel

  // Actuator
  sealed trait Result

  case class Transfer(
      sender: String,
      receiver: String,
      amount: Double,
      currency: Currency,
      date: Instant,
      desc: String,
      id: Option[Long] = scala.None
  )

  case class Aircraft(
      registration: String,
      age: Int,
      model: AircraftModel,
      id: Long
  )

  case object EUR extends Currency

  case object USD extends Currency

  case object AirbusA320 extends AircraftModel

  case object AirbusA320N extends AircraftModel

  case object AirbusA332 extends AircraftModel

  case object AirbusA333 extends AircraftModel

  case object Boeing737NG extends AircraftModel

  case object Boeing788 extends AircraftModel

  case object Success extends Result

  case object Warning extends Result

  case object Error extends Result

  // HELPERS

}
