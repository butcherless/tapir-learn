package com.cmartin.learn.domain

import com.cmartin.learn.api.ApiCodecs.CurrencySelector
import com.cmartin.learn.api.ApiModel.{AircraftDto, ApiBuildInfo, TransferDto}
import com.cmartin.learn.domain.DomainModel.{AirbusA320, AirbusA332, Aircraft, AircraftModel, Boeing737NG, Boeing788, BuildInfo, Result, Transfer}

trait ApiConverters {

  case class CustomMappingError(message: String) extends RuntimeException(message)

  // ACTUATOR

  implicit def stringToResult(s: String): Result = s match {
    case "Success" => DomainModel.Success
    case "Warning" => DomainModel.Warning
    case "Error" => DomainModel.Error
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

  def apiToModel(a: AircraftDto): Aircraft =
    Aircraft(a.registration, a.age, a.model, a.id.getOrElse(0))

  def modelToApi(a: Aircraft): AircraftDto =
    AircraftDto(a.registration, a.age, a.model.toString, Some(a.id))


  // TRANSFER
  def apiToModel(dto: TransferDto): Transfer =
    Transfer(
      dto.sender,
      dto.receiver,
      dto.amount,
      dto.currency.toCurrency,
      dto.desc,
    )

  def modelToApi(entity: Transfer): TransferDto =
    TransferDto(
      entity.sender,
      entity.receiver,
      entity.amount,
      entity.currency.toString,
      entity.desc
    )


  private def manageEmptyCase(entity: String) =
    throw CustomMappingError(s"empty value for $entity")

  private def manageDefaultCase(entity: String, value: String) =
    throw CustomMappingError(s"invalid $entity value: $value")

}

object ApiConverters extends ApiConverters
