package com.cmartin.learn.domain

import com.cmartin.learn.api.ApiModel.{ApiAircraft, ApiBuildInfo}
import com.cmartin.learn.domain.DomainModel.{AirbusA320, AirbusA332, Aircraft, AircraftModel, Boeing737NG, Boeing788, BuildInfo, Error, Result, Success, Warning}

trait ApiConverters {

  case class CustomMappingError(message: String) extends RuntimeException(message)

  // ACTUATOR

  implicit def stringToResult(s: String): Result = s match {
    case "Success" => Success
    case "Warning" => Warning
    case "Error" => Error
    case "" => manageEmptyCase("result")
    case _ => manageDefaultCase("result", s)
  }

  def apiToModel(info: ApiBuildInfo): BuildInfo =
    BuildInfo(info.appName, info.date, info.version, info.result)

  def modelToApi(info: BuildInfo): ApiBuildInfo =
    ApiBuildInfo(info.appName, info.date, info.version, info.result.toString)


  // AIRCRAFT
  implicit def stringToAircraftModel(s: String): AircraftModel = s match {
    case "AirbusA320" => AirbusA320
    case "AirbusA332" => AirbusA332
    case "Boeing737NG" => Boeing737NG
    case "Boeing788" => Boeing788
    case "" => manageEmptyCase("model")
    case _ => manageDefaultCase("model", s)
  }

  def apiToModel(a: ApiAircraft): Aircraft =
    Aircraft(a.registration, a.age, a.model, a.id.getOrElse(0))

  def modelToApi(a: Aircraft): ApiAircraft =
    ApiAircraft(a.registration, a.age, a.model.toString, Some(a.id))


  private def manageEmptyCase(entity: String) =
    throw CustomMappingError(s"empty value for $entity")

  private def manageDefaultCase(entity: String, value: String) =
    throw CustomMappingError(s"invalid $entity value: $value")
}

object ApiConverters extends ApiConverters
