package com.cmartin.learn.api

import com.cmartin.learn.api.ApiModel.{Currency, Output, PerStrategy, Result}
import io.circe.{Encoder, Json}

trait ApiCodecs {
  def genericEncoder[T](): Encoder[T] = (a: T) => Json.fromString(a.toString())


  // Object => JSON
  implicit lazy val currencyEncoder: Encoder[Currency] =
    genericEncoder[Currency]()

  implicit lazy val resultEncoder: Encoder[Result] =
    genericEncoder[Result]()

  implicit lazy val outputEncoder: Encoder[Output] =
    genericEncoder[Output]()

  implicit lazy val perStrategyEncoder: Encoder[PerStrategy] =
    genericEncoder[PerStrategy]()


}
