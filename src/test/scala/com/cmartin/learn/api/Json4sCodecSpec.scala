package com.cmartin.learn.api

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class Json4sCodecSpec extends AnyFlatSpec with Matchers {

  behavior of "Json4s codec"

  it should "TODO: circe implementation" in {
    import sttp.tapir._
    import sttp.tapir.json.circe._
    import io.circe.generic.auto._

    case class Book(author: String, title: String, year: Int)

    val bookInput: EndpointIO[Book] = jsonBody[Book]

    info(bookInput.show)
    info(bookInput.toString)
  }

  it should "TODO: json4s implementation" in {
    import sttp.tapir._
    import sttp.tapir.json.json4s._

    case class Book(author: String, title: String, year: Int)

    val bookInput: EndpointIO[Book] = jsonBody[Book]

    info(bookInput.show)
    info(bookInput.toString)
  }

}