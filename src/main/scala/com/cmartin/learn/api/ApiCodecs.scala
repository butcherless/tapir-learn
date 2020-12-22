package com.cmartin.learn.api

import com.cmartin.learn.api.Model._
import com.cmartin.learn.domain.Model._
import io.circe.{Decoder, Encoder, HCursor, Json}

trait ApiCodecs {

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

  implicit class AircraftModelSelector(model: String) {
    def toAircraftModel: AircraftModel =
      model match {
        case "AirbusA320"  => AirbusA320
        case "AirbusA332"  => AirbusA332
        case "Boeing737NG" => Boeing737NG
        case "Boeing788"   => Boeing788
        // TODO error cases
      }
  }

  implicit lazy val aircraftDecoder: Decoder[Aircraft] = new Decoder[Aircraft] {
    override def apply(c: HCursor): Decoder.Result[Aircraft] = {
      for {
        registration <- c.get[String]("registration")
        age          <- c.get[Int]("age")
        model        <- c.get[String]("model")
        id           <- c.get[Long]("id")
      } yield Aircraft(registration, age, model.toAircraftModel, id)
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

  //  Custom decoder

  //  implicit lazy val transferDecoder: Decoder[Transfer] = new Decoder[Transfer] {
  //    override def apply(c: HCursor): Decoder.Result[Transfer] = {
  //      for {
  //        sender   <- c.get[String]("sender")
  //        receiver <- c.get[String]("receiver")
  //        amount   <- c.get[Double]("amount")
  //        currency <- c.get[String]("currency")
  //        date     <- c.get[Instant]("date")
  //        desc     <- c.get[String]("desc")
  //      } yield Transfer(sender, receiver, amount, currency.toCurrency, date, desc)
  //    }
  //  }

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

  // JSON => Object
  //  implicit lazy val currencyDecoder: Decoder[Currency] = new Decoder[Currency] {
  //    override def apply(c: HCursor): Decoder.Result[Currency] = {
  //      println(c.values)
  //      for {
  //        obj <- c.get[Currency]("currency")
  //      } yield select(obj)
  //    }
  //  }

  //TODO refactor to generic select[T]
  def select(o: Currency): Currency =
    o match {
      case c @ EUR => c
      case c @ USD => c
    }

  //TODO https://github.com/circe/circe/blob/274ff5928d3784b8fc8e0ac2e9015c2d5b998b25/modules/tests/shared/src/test/scala/io/circe/JavaTimeCodecSuite.scala

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
      case o @ ComOut => o
      case o @ ShaOut => o
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

}

object ApiCodecs extends ApiCodecs
