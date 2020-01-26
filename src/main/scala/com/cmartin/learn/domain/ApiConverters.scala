package com.cmartin.learn.domain

import com.cmartin.learn.api.ApiModel.{ApiAircraft, ApiBuildInfo}
import com.cmartin.learn.domain.DomainModel.{Aircraft, AircraftModel, Boeing737NG, Boeing788, BuildInfo, Error, Result, Success, Warning}

trait ApiConverters {

  case class CustomMappingError(message: String) extends RuntimeException(message)

  // ACTUATOR

  implicit def stringToResult(s: String): Result = s match {
    case "Success" => Success
    case "Warning" => Warning
    case "Error" => Error
    case "" => throw CustomMappingError(s"empty value for result")
    case _ => throw CustomMappingError(s"invalid result value: $s")
  }

  def apiToModel(info: ApiBuildInfo): BuildInfo =
    BuildInfo(info.appName, info.date, info.version, info.result)

  def modelToApi(info: BuildInfo): ApiBuildInfo =
    ApiBuildInfo(info.appName, info.date, info.version, info.result.toString)


  // AIRCRAFT
  implicit def stringToAircraftModel(s: String): AircraftModel = s match {
    case "Boeing788" => Boeing788
    case "Boeing737NG" => Boeing737NG
    case "" => throw CustomMappingError(s"empty value for aircraft model")
    case _ => throw CustomMappingError(s"invalid aircraft model: $s")
  }

  def apiToModel(a: ApiAircraft): Aircraft =
    Aircraft(a.registration, a.age, a.model, a.id.getOrElse(0))

  def modelToApi(a: Aircraft): ApiAircraft =
    ApiAircraft(a.registration, a.age, a.model.toString, Some(a.id))


}

object ApiConverters extends ApiConverters
