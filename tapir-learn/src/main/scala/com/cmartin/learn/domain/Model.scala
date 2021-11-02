package com.cmartin.learn.domain

import com.cmartin.learn.domain.Model.AircraftModel.AircraftModel

import java.time.Instant

object Model {

  sealed trait Currency

  // ADT
  // sealed trait AircraftModel

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

  object AircraftModel extends Enumeration {
    type AircraftModel = Value
    val AirbusA320, AirbusA320N, AirbusA332, AirbusA333, Boeing737NG, Boeing788, Boeing789 = Value
  }

  case object Success extends Result

  case object Warning extends Result

  case object Error extends Result

}
