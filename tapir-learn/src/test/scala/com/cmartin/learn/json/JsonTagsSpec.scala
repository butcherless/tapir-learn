package com.cmartin.learn.json

import org.json4s._
import org.json4s.native.JsonMethods._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class JsonTagsSpec extends AnyFlatSpec with Matchers {

  import JsonTagsSpec._

  behavior of "Json tags"

  it should "return an empty list" in {
    val jsonA = parse(emptyJsonA)
    val jsonB = parse(emptyJsonB)

    val result: JValue = jsonA merge jsonB

    info(result.toString)

    result shouldBe JArray(List())
  }

  it should "return tag-1 after merge" in {
    val jsonA = parse(emptyJsonA)
    val jsonB = parse(singleTagJson)

    val result: JValue = jsonA merge jsonB

    info(result.toString)

    result shouldBe JArray(List(JString("tag-1")))
  }

  it should "return 2 tags" in {
    val jsonA = parse(emptyJsonA)
    val jsonB = parse(twoTagJson)

    val result: JValue = jsonA merge jsonB

    info(result.toString)

    result shouldBe JArray(List(JString("tag-1"), JString("tag-2")))
  }

  it should "return a single tag after diff" in {
    val jsonA = parse(twoTagJson)
    val jsonB = parse(threeTagJson)

    val result: Diff = jsonA diff jsonB

    info(result.toString)

    // result tuple (changed, added, deleted)
    result shouldBe Diff(JNothing, JArray(List(JString("tag-3"))), JNothing)
  }

  it should "return diff between threeTagJson and fourTagJson" in {
    val jsonA = parse(threeTagJson)
    val jsonB = parse(fourTagJson)

    val result: Diff = jsonA diff jsonB

    info(result.toString)

    // (changed, added, deleted)
    result shouldBe Diff(JString("tag-4"), JArray(List(JString("tag-5"))), JNothing)
  }

  it should "return diff between fourTagJson and threeTagJson" in {
    val jsonA = parse(fourTagJson)
    val jsonB = parse(threeTagJson)

    val result: Diff = jsonA diff jsonB

    info(result.toString)

    // (changed, added, deleted)
    result shouldBe Diff(JString("tag-3"), JNothing, JArray(List(JString("tag-5"))))
  }

}

object JsonTagsSpec {
  val emptyJsonA = "[]"
  val emptyJsonB = "[]"

  val singleTagJson =
    """
      |["tag-1"]
      |""".stripMargin

  val twoTagJson =
    """
      |["tag-1","tag-2"]
      |""".stripMargin

  val threeTagJson =
    """
      |["tag-1","tag-2","tag-3"]
      |""".stripMargin

  val fourTagJson =
    """
      |["tag-1","tag-2","tag-4","tag-5"]
      |""".stripMargin
}
