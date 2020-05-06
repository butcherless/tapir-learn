package com.cmartin.learn.domain

import java.time.{Clock, LocalDateTime}

import com.cmartin.learn.api.ApiModel.{APP_NAME, APP_VERSION}

object DomainModel {

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

  // ADT
  sealed trait AircraftModel

  case object AirbusA320 extends AircraftModel

  case object AirbusA332 extends AircraftModel

  case object Boeing737NG extends AircraftModel

  case object Boeing788 extends AircraftModel

  case class Aircraft(
      registration: String,
      age: Int,
      model: AircraftModel,
      id: Long
  )

  // Actuator
  sealed trait Result

  case object Success extends Result

  case object Warning extends Result

  case object Error extends Result

  // HELPERS

}
