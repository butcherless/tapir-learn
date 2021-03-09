package com.cmartin.learn.domain

import com.cmartin.learn.api
import com.cmartin.learn.api.ApiCodecs.CurrencySelector
import com.cmartin.learn.api.BuildInfo
import com.cmartin.learn.api.Model.AircraftType._
import com.cmartin.learn.api.Model.{AircraftDto, BuildInfoDto, TransferDto}
import com.cmartin.learn.domain.Model._

import java.time._

trait ApiConverters {

  implicit class AircraftTypeToModel(aType: AircraftType) {
    def toModel: AircraftModel.Value = aType match {
      case AirbusA320  => AircraftModel.AirbusA320
      case AirbusA320N => AircraftModel.AirbusA320N
      case Airbus332   => AircraftModel.AirbusA332
      case AirbusA333  => AircraftModel.AirbusA333
      case Boeing737NG => AircraftModel.Boeing737NG
      case Boeing788   => AircraftModel.Boeing788
      case Boeing789   => AircraftModel.Boeing789
    }
  }

  //TODO
  def apiToModel(a: AircraftDto): Aircraft = {

    Aircraft(a.registration, a.age, a.model.toModel, a.id.getOrElse(0))

  }

  // ACTUATOR

  implicit def stringToResult(s: String): Result =
    s match {
      case "Success" => Model.Success
      case "Warning" => Model.Warning
      case "Error"   => Model.Error
      case ""        => manageEmptyCase("result")
      case _         => manageDefaultCase("result", s)
    }

  // AIRCRAFT
//  implicit def stringToAircraftModel(s: String): AircraftModel =
//    s match {
//      case "AirbusA320"  => AirbusA320
//      case "AirbusA320N" => AirbusA320N
//      case "AirbusA332"  => AirbusA332
//      case "AirbusA333"  => AirbusA333
//      case "Boeing737NG" => Boeing737NG
//      case "Boeing788"   => Boeing788
//      case ""            => manageEmptyCase("model")
//      case _             => manageDefaultCase("model", s)
//    }

  //TODO
  def modelToApi(a: Aircraft): AircraftDto =
    AircraftDto(a.registration, a.age, api.Model.AircraftType.Airbus332, Some(a.id))

  def apiToModel(dto: TransferDto): Transfer =
    Transfer(
      dto.sender,
      dto.receiver,
      dto.amount,
      dto.currency.toCurrency,
      dto.date,
      dto.desc
    )

  // TRANSFER

  implicit class TransferDtoOps(dto: TransferDto) {
    def toModel: Transfer =
      Transfer(
        dto.sender,
        dto.receiver,
        dto.amount,
        dto.currency.toCurrency,
        dto.date,
        dto.desc
      )
  }

  implicit class TransferOps(entity: Transfer) {
    def toApi: TransferDto =
      TransferDto(
        entity.sender,
        entity.receiver,
        entity.amount,
        entity.currency.toString,
        entity.date,
        entity.desc
      )
  }

  def modelToApi(entity: Transfer): TransferDto =
    TransferDto(
      entity.sender,
      entity.receiver,
      entity.amount,
      entity.currency.toString,
      entity.date,
      entity.desc
    )

  def modelToApi(): BuildInfoDto = {
    BuildInfoDto(
      BuildInfo.name,
      BuildInfo.version,
      BuildInfo.scalaVersion,
      BuildInfo.sbtVersion,
      BuildInfo.gitCommit,
      LocalDateTime
        .ofInstant(
          Instant.ofEpochMilli(BuildInfo.builtAtMillis),
          ZoneId.systemDefault()
        )
    )
  }

  case class CustomMappingError(message: String) extends RuntimeException(message)

  private def manageEmptyCase(entity: String) =
    throw CustomMappingError(s"empty value for $entity")

  private def manageDefaultCase(entity: String, value: String) =
    throw CustomMappingError(s"invalid $entity value: $value")
}

object ApiConverters extends ApiConverters
