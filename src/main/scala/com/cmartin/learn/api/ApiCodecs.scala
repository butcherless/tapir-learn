package com.cmartin.learn.api

import com.cmartin.learn.api.ApiModel._
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
    override def apply(c: HCursor): Decoder.Result[Currency] = {
      println(c.values)
      for {
        obj <- c.get[Currency]("currency")
      } yield select(obj)
    }
  }

  implicit class CurrencySelector(currency: String) {
    def toCurrency: Currency =
      currency match {
        case "EUR" => EUR
        case "USD" => USD
        case "" => throw new RuntimeException(s"currency is mandadory")
        case _ => throw new RuntimeException(s"not a valid currency: $currency")
      }
  }



  implicit lazy val transferDecoder: Decoder[Transfer] = new Decoder[Transfer] {
    override def apply(c: HCursor): Decoder.Result[Transfer] = {
      for {
        sender <- c.get[String]("sender")
        receiver <- c.get[String]("receiver")
        amount <- c.get[Double]("amount")
        currency <- c.get[String]("currency")
        desc <- c.get[String]("desc")
      } yield Transfer(sender, receiver, amount, currency.toCurrency, desc)
    }
  }


  //TODO refactor to generic select[T]
  def select(o: Currency): Currency = o match {
    case c@EUR => c
    case c@USD => c
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
    case o@ComOut => o
    case o@ShaOut => o
  }

  /*
      C O M  S T R A T E G Y   C O D E C
  */

  // Object => JSON
  implicit lazy val comStrategyEncoder: Encoder[ComStrategy] =
    genericEncoder[ComStrategy]()

  // JSON => Object
  implicit lazy val comStrategyDecoder: Decoder[ComStrategy] = (c: HCursor) => for {
    strategy <- c.get[ComStrategy]("comStrategy")
  } yield strategy match {
    case s@Append => s
    case s@Merge => s
    case s@None => s
  }

  /*
      P E R  S T R A T E G Y   C O D E C
  */

  // Object => JSON
  implicit lazy val perStrategyEncoder: Encoder[PerStrategy] =
    genericEncoder[PerStrategy]()

  // JSON => Object
  implicit lazy val perStrategyDecoder: Decoder[PerStrategy] = (c: HCursor) => for {
    strategy <- c.get[PerStrategy]("perStrategy")
  } yield strategy match {
    case s@OveStrategy => s
    case s@ShaStrategy => s
  }

}
