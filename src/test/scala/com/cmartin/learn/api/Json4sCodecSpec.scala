package com.cmartin.learn.api

import org.json4s.JsonAST.JValue
import org.json4s.native.JsonMethods
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sttp.tapir.generic.auto._

import java.util.Date

class Json4sCodecSpec extends AnyFlatSpec with Matchers {

  behavior of "Json4s codec"

  import sttp.tapir._
  import sttp.tapir.json.json4s._

  it should "json4s implementation" in {

    case class Book(author: String, title: String, year: Int)

    val bookInput: EndpointIO[Book] = jsonBody[Book]
  }

  val number   = 1234L
  val idJson   = """{"id":1234}"""
  val idJValue = JsonMethods.parse(idJson)
  val myId     = MyId(1234)

  case class MyId(id: Int)

  it should "decode a json to a JValue" in {
    val decoded = json4sCodec[JValue].decode(idJson)

    decoded match {
      case failure: DecodeResult.Failure => fail(failure.toString)
      case DecodeResult.Value(value) =>
        value shouldBe idJValue
    }
  }

  it should "decode a json to a case class" in {
    val decoded = json4sCodec[MyId].decode(idJson)

    decoded match {
      case failure: DecodeResult.Failure => fail(failure.toString)
      case DecodeResult.Value(value) =>
        value.id shouldBe myId.id
    }
  }

  it should "encode Long value" in {
    val encoded: String = json4sCodec[Long].encode(number)
    encoded shouldBe "1234"
  }

  it should "encode a Date value" in {
    val s = json4sCodec[Date].encode(new Date())
    s should fullyMatch regex """"[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}Z"""".r
  }

  it should "encode a case class value" in {
    val encoded = json4sCodec[MyId].encode(myId)
    encoded shouldBe idJson
  }

  it should "encode a JValue" in {
    val id      = idJValue
    val encoded = json4sCodec[JValue].encode(id)
    encoded shouldBe idJson
  }

}
