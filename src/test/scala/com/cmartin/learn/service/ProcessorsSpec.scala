package com.cmartin.learn.service

import com.cmartin.learn.domain.ProcessorModel.GenericDerivation.{eventDecoder, eventEncoder}
import com.cmartin.learn.domain.ProcessorModel._
import com.cmartin.learn.service.messaging.MyMessaging
import io.circe
import io.circe.parser.{decode, parse}
import io.circe.syntax._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.ZIO

class ProcessorsSpec extends AnyFlatSpec with Matchers {

  import ProcessorsSpec._

  val filterEvent: Event = FilterEvent("features.speed > 100", "ip.filter", "rp.filter")
  val jsltEvent: Event   = JsltEvent("dummy jslt event", "ip.jslt", "rp.jslt")
  val restEvent: Event   = RestEvent("dummy rest event", "ip.rest", "rp.rest")

  val filterEventJson =
    """{"predicate":"features.speed > 100","inputPath":"ip.filter","resultPath":"rp.filter","name":"filter"}"""
  val jsltEventJson = """{"transform":"dummy jslt event","inputPath":"ip.jslt","resultPath":"rp.jslt","name":"jslt"}"""
  val restEventJson = """{"getUrl":"dummy rest event","inputPath":"ip.rest","resultPath":"rp.rest","name":"rest"}"""

  val eventsJson = s"""[$filterEventJson,$jsltEventJson,$restEventJson]"""

  val runtime = zio.Runtime.default

  behavior of "Processor Sequence"

  it should "A1 handle a list of processors" in {
    // given
    val filterEvent = FilterEvent("dummy filter event")
    val jsltEvent   = JsltEvent("dummy jslt event")
    val restEvent   = RestEvent("dummy rest event")

    val processors: Seq[Processor[Event]] =
      Seq(
        FilterProcessor(filterEvent),
        JsltProcessor(jsltEvent),
        RestProcessor(restEvent)
      )

    // when
    val p = for {
      rs <- MyMessaging.handle(processors)
    } yield rs

    // then
    val result: Seq[String] = runProgram(p)

    result shouldBe Seq("dummy filter event", "dummy jslt event", "dummy rest event")
  }

  it should "B1 encode a filter event, object -> json" in {
    val feJson = filterEvent.asJson.noSpaces
    feJson shouldBe filterEventJson
  }

  it should "B2 encode a jslt event, object -> json" in {
    val jeJson = jsltEvent.asJson.noSpaces
    jeJson shouldBe jsltEventJson
  }

  it should "B3 encode a rest event, object -> json" in {
    val reJson = restEvent.asJson.noSpaces
    reJson shouldBe restEventJson
  }

  it should "B4 encode all Events, object -> json" in {
    val events = Seq(filterEvent, jsltEvent, restEvent).asJson.noSpaces
    events shouldBe eventsJson
  }

  it should "C1 decode a filter event, json -> object" in {
    val fEvent: Event = decodeEvent(filterEventJson)
    fEvent shouldBe filterEvent
  }

  it should "C2 decode a jslt event, json -> object" in {
    val jEvent: Event = decodeEvent(jsltEventJson)
    jEvent shouldBe jsltEvent
  }

  it should "C3 decode a rest event, json -> object" in {
    val rEvent: Event = decodeEvent(restEventJson)
    rEvent shouldBe restEvent
  }

  it should "C4 decode all Events" in {
    val events: Seq[Event] = decodeEvents(eventsJson)

    events shouldBe Seq(filterEvent, jsltEvent, restEvent)
  }

  it should "D1 parse a sequence of processors" in {
    val parsedProcessors = parse(processorsJson)

    parsedProcessors.isRight shouldBe true
    parsedProcessors map { json =>
      info(json.asArray.toString())
    //TODO json.asArray shouldBe  Vector(filterEvent.asJson, jsltEvent.asJson, restEvent.asJson)
    }
  }

  def decodeEvent(eventString: String): Event =
    decode[Event](eventString)
      .fold(
        e => fail(e.getMessage),
        identity
      )

  def decodeEvents(eventsString: String): Seq[Event] =
    decode[Seq[Event]](eventsString)
      .fold(
        e => fail(e.getMessage),
        identity
      )

  def getEvent[T](eventEither: Either[circe.Error, T]): T =
    eventEither.fold(e => fail(e.getMessage), identity)

  def runProgram[T](p: ZIO[MyMessaging, Throwable, T]) =
    runtime
      .unsafeRun(
        p.provideLayer(MyMessaging.live)
      )

}

object ProcessorsSpec {
  val processorsJson =
    """
      |[
      |  {
      |    "name": "filter",
      |    "predicate": "features.speed > 100",
      |    "inputPath": "key1.key2",
      |    "resultPath": "key3",
      |    "outputPath": "key4"
      |
      |  },
      |  {
      |    "name": "jslt",
      |    "transform": "let idparts = split(.id, \"-\")\nlet xxx = [for ($idparts) \"x\" * size(.)]\n{\"id\" : join($xxx, \"-\"),\"type\" : \"Anonymized-View\",\n* : .\n}\"\n}",
      |    "inputPath": "key1.key2",
      |    "resultPath": "key3",
      |    "outputPath": "key4"
      |
      |  },
      |  {
      |    "name": "jolt",
      |    "input": {
      |      "id": 1234,
      |      "name": "device name"
      |    },
      |    "spec": "",
      |    "inputPath": "key1.key2",
      |    "resultPath": "key3",
      |    "outputPath": "key4"
      |
      |  },
      |  {
      |    "name": "rest",
      |    "method": "get",
      |    "url": "http://localhost:8080/health",
      |    "inputPath": "key1.key2",
      |    "resultPath": "key3",
      |    "outputPath": "key4"
      |  }
      |]
      |""".stripMargin
}
