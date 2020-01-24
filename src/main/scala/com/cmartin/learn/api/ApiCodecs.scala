package com.cmartin.learn.api

import com.cmartin.learn.api.ApiModel.{ComOut, Currency, EUR, Error, Output, OveStrategy, PerStrategy, Result, ShaOut, ShaStrategy, Success, USD, Warning}
import io.circe.{Decoder, Encoder, HCursor, Json}

trait ApiCodecs {
  def genericEncoder[T](): Encoder[T] = new Encoder[T] {
    override def apply(a: T): Json =
      Json.fromString(a.toString())
  }


  /*
      C U R R E N C Y   C O D E C
  */

  // Object => JSON
  implicit lazy val currencyEncoder: Encoder[Currency] =
    genericEncoder[Currency]()

  // JSON => Object
  implicit lazy val currencyDecoder: Decoder[Currency] = new Decoder[Currency] {
    override def apply(c: HCursor): Decoder.Result[Currency] =
      for {
        obj <- c.get[Currency]("currency")
      } yield select(obj)
  }

  //TODO refactor to generic select[T]
  def select(o: Currency): Currency = o match {
    case EUR => EUR
    case USD => USD
  }

  /*
      R E S U L T   C O D E C
  */

  // Object => JSON
  implicit lazy val resultEncoder: Encoder[Result] =
    genericEncoder[Result]()

  // JSON => Object
  implicit lazy val resultDecoder: Decoder[Result] = (c: HCursor) => for {
    obj <- c.get[Result]("result")
  } yield select(obj)

  def select(o: Result): Result = o match {
    case Success => Success
    case Warning => Warning
    case Error => Error
  }

  //TODO https://github.com/circe/circe/blob/274ff5928d3784b8fc8e0ac2e9015c2d5b998b25/modules/tests/shared/src/test/scala/io/circe/JavaTimeCodecSuite.scala

  /*
      O U T P U T   C O D E C
  */

  // Object => JSON
  implicit lazy val outputEncoder: Encoder[Output] =
    genericEncoder[Output]()

  // JSON => Object
  implicit lazy val outputDecoder: Decoder[Output] = (c: HCursor) => for {
    strategy <- c.get[Output]("output")
  } yield strategy match {
    case ComOut => ComOut
    case ShaOut => ShaOut
  }

  /*
      O U T P U T   C O D E C
  */

  // Object => JSON
  implicit lazy val perStrategyEncoder: Encoder[PerStrategy] =
    genericEncoder[PerStrategy]()

  // JSON => Object
  implicit lazy val perStrategyDecoder: Decoder[PerStrategy] = (c: HCursor) => for {
    strategy <- c.get[PerStrategy]("perStrategy")
  } yield strategy match {
    case OveStrategy => OveStrategy
    case ShaStrategy => ShaStrategy
  }

}
