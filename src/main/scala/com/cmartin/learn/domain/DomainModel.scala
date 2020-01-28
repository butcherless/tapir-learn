package com.cmartin.learn.domain

import java.time.{Clock, LocalDateTime}

import com.cmartin.learn.api.ApiModel.{APP_NAME, APP_VERSION}


object DomainModel {

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


  case class BuildInfo(appName: String,
                       date: String,
                       version: String,
                       result: Result
                      )


  // HELPERS
  def buildInfo(): BuildInfo =
    BuildInfo(
      APP_NAME,
      LocalDateTime.now(Clock.systemDefaultZone()).toString,
      APP_VERSION,
      Success
    )

}
