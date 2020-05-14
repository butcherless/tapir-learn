package com.cmartin.learn.service

import com.cmartin.learn.domain.ProcessorModel.GenericDerivation.{decodeEvent, encodeEvent}
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

  val filterEvent: Event = FilterEvent("features.speed > 100")
  val jsltEvent: Event   = JsltEvent("dummy jslt event")
  val restEvent: Event   = RestEvent("dummy rest event")

  val filterEventJson = """{"predicate":"features.speed > 100","name":"filter"}"""
  val jsltEventJson   = """{"transform":"dummy jslt event","name":"jslt"}"""
  val restEventJson   = """{"getUrl":"dummy rest event","name":"rest"}"""

  val eventsJson = s"""[$filterEventJson,$jsltEventJson,$restEventJson]"""

  val runtime = zio.Runtime.default

  behavior of "Processor Sequence"

  it should "T1 handle a list of processors" in {
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

  it should "T2 encode all Events" in {

    val feJson = filterEvent.asJson.noSpaces
    feJson shouldBe filterEventJson

    val jeJson = jsltEvent.asJson.noSpaces
    jeJson shouldBe jsltEventJson

    val reJson = restEvent.asJson.noSpaces
    reJson shouldBe restEventJson

    val events = Seq(filterEvent, jsltEvent, restEvent).asJson.noSpaces
    events shouldBe eventsJson
  }

  it should "T3 decode all Events" in {

    val filterEventEither: Either[circe.Error, Event] =
      decode[Event](filterEventJson)
    val fEvent: Event = getEvent(filterEventEither)

    fEvent shouldBe filterEvent

    val jEvent: Event = getEvent(decode[Event](jsltEventJson))
    jEvent shouldBe jsltEvent
    val rEvent: Event = getEvent(decode[Event](restEventJson))

    rEvent shouldBe restEvent

    val events: Seq[Event] = getEvent(decode[Seq[Event]](eventsJson))

    events shouldBe Seq(filterEvent, jsltEvent, restEvent)
  }

  it should "T4 parse a sequence of processors" in {
    val parsedProcessors = parse(processorsJson)

    parsedProcessors.isRight shouldBe true
    parsedProcessors map { json =>
      info(json.asArray.toString())
    }
  }

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
      |    "type": "filter",
      |    "predicate": "features.speed > 100"
      |  },
      |  {
      |    "type": "jslt",
      |    "transform": "let idparts = split(.id, \"-\")\nlet xxx = [for ($idparts) \"x\" * size(.)]\n{\"id\" : join($xxx, \"-\"),\"type\" : \"Anonymized-View\",\n* : .\n}\"\n}"
      |  },
      |  {
      |    "type": "jolt",
      |    "input": {
      |      "id": 1234,
      |      "name": "device name"
      |    },
      |    "spec": ""
      |  },
      |  {
      |    "type": "rest",
      |    "method": "get",
      |    "url": "http://localhost:8080/health"
      |  }
      |]
      |""".stripMargin
}
