package com.cmartin.learn.domain

import java.time.Instant
import java.time.LocalDateTime

import com.cmartin.learn.api.ApiCodecs.CurrencySelector
import com.cmartin.learn.api.BuildInfo
import com.cmartin.learn.api.Model.AircraftDto
import com.cmartin.learn.api.Model.BuildInfoDto
import com.cmartin.learn.api.Model.TransferDto
import com.cmartin.learn.domain.Model._

trait ApiConverters {

  case class CustomMappingError(message: String) extends RuntimeException(message)

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
  implicit def stringToAircraftModel(s: String): AircraftModel =
    s match {
      case "AirbusA320"  => AirbusA320
      case "AirbusA320N" => AirbusA320N
      case "AirbusA332"  => AirbusA332
      case "AirbusA333"  => AirbusA333
      case "Boeing737NG" => Boeing737NG
      case "Boeing788"   => Boeing788
      case ""            => manageEmptyCase("model")
      case _             => manageDefaultCase("model", s)
    }

  def apiToModel(a: AircraftDto): Aircraft =
    Aircraft(a.registration, a.age, a.model, a.id.getOrElse(0))

  def modelToApi(a: Aircraft): AircraftDto =
    AircraftDto(a.registration, a.age, a.model.toString, Some(a.id))

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

  def apiToModel(dto: TransferDto): Transfer =
    Transfer(
      dto.sender,
      dto.receiver,
      dto.amount,
      dto.currency.toCurrency,
      dto.date,
      dto.desc
    )

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
      Instant.ofEpochMilli(BuildInfo.builtAtMillis)
    )
  }

  private def manageEmptyCase(entity: String) =
    throw CustomMappingError(s"empty value for $entity")

  private def manageDefaultCase(entity: String, value: String) =
    throw CustomMappingError(s"invalid $entity value: $value")
}

object ApiConverters extends ApiConverters
