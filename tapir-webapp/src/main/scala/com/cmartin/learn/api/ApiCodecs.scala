package com.cmartin.learn.api

import com.cmartin.learn.api.Model._
import com.cmartin.learn.domain.Model._
import io.circe.Decoder
import io.circe.Encoder
import io.circe.HCursor
import io.circe.Json
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.json4s.DefaultFormats
import sttp.tapir.Schema
import sttp.tapir.generic.auto._

trait ApiCodecs {

  implicit val formats: DefaultFormats.type = org.json4s.DefaultFormats

  implicit lazy val aircraftEncoder: Encoder[Aircraft] = new Encoder[Aircraft] {
    override def apply(a: Aircraft): Json = {
      Json.obj(
        ("registration", Json.fromString(a.registration)),
        ("age", Json.fromInt(a.age)),
        ("model", Json.fromString(a.model.toString)),
        ("id", Json.fromLong(a.id))
      )
    }
  }

  implicit class CurrencySelector(currency: String) {
    def toCurrency: Currency =
      currency match {
        case "EUR" => EUR
        case "USD" => USD
        case ""    => throw new RuntimeException(s"currency is mandatory")
        case _     => throw new RuntimeException(s"not a valid currency: $currency")
      }
  }

  def genericEncoder[T](): Encoder[T] =
    new Encoder[T] {
      override def apply(a: T): Json =
        Json.fromString(a.toString)
    }

  /*
      C U R R E N C Y   C O D E C
   */

  // Object => JSON
  implicit lazy val currencyEncoder: Encoder[Currency] =
    genericEncoder[Currency]()

  // TODO refactor to generic select[T]
  def select(o: Currency): Currency =
    o match {
      case c @ EUR => c
      case c @ USD => c
    }

  // TODO https://github.com/circe/circe/blob/274ff5928d3784b8fc8e0ac2e9015c2d5b998b25/modules/tests/shared/src/test/scala/io/circe/JavaTimeCodecSuite.scala

  /*
      O U T P U T   C O D E C
   */

  // Object => JSON
  implicit lazy val outputEncoder: Encoder[Output] =
    genericEncoder[Output]()

  // JSON => Object
  implicit lazy val outputDecoder: Decoder[Output] = (c: HCursor) =>
    for {
      strategy <- c.get[Output]("output")
    } yield strategy match {
      case o @ ComOut() => o
      case o @ ShaOut() => o
    }

  /*
      C O M  S T R A T E G Y   C O D E C
   */

  // Object => JSON
  implicit lazy val comStrategyEncoder: Encoder[ComStrategy] =
    genericEncoder[ComStrategy]()

  // JSON => Object
  implicit lazy val comStrategyDecoder: Decoder[ComStrategy] = (c: HCursor) =>
    for {
      strategy <- c.get[ComStrategy]("comStrategy")
    } yield strategy match {
      case s @ Append => s
      case s @ Merge  => s
      case s @ None   => s
    }

  /*
      P E R  S T R A T E G Y   C O D E C
   */

  // Object => JSON
  implicit lazy val perStrategyEncoder: Encoder[PerStrategy] =
    genericEncoder[PerStrategy]()

  // JSON => Object
  implicit lazy val perStrategyDecoder: Decoder[PerStrategy] = (c: HCursor) =>
    for {
      strategy <- c.get[PerStrategy]("perStrategy")
    } yield strategy match {
      case s @ OveStrategy => s
      case s @ ShaStrategy => s
    }

  /*
      S I D S   C O D E C
   */
  // circe-generic-auto's Scala 3 macro doesn't reliably chain through this nested
  // Processors/Source/State/Sids hierarchy, so derive each level explicitly instead.

  implicit lazy val processorsEncoder: Encoder[Processors] = deriveEncoder[Processors]
  implicit lazy val processorsDecoder: Decoder[Processors] = deriveDecoder[Processors]
  implicit lazy val processorsSchema: Schema[Processors]   = Schema.derived[Processors]

  implicit lazy val sourceEncoder: Encoder[Source] = deriveEncoder[Source]
  implicit lazy val sourceDecoder: Decoder[Source] = deriveDecoder[Source]
  implicit lazy val sourceSchema: Schema[Source]   = Schema.derived[Source]

  implicit lazy val stateEncoder: Encoder[State] = deriveEncoder[State]
  implicit lazy val stateDecoder: Decoder[State] = deriveDecoder[State]
  implicit lazy val stateSchema: Schema[State]   = Schema.derived[State]

  implicit lazy val sidsEncoder: Encoder[Sids] = deriveEncoder[Sids]
  implicit lazy val sidsDecoder: Decoder[Sids] = deriveDecoder[Sids]
  implicit lazy val sidsSchema: Schema[Sids]   = Schema.derived[Sids]

}

object ApiCodecs extends ApiCodecs
