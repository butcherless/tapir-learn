package com.cmartin.learn.service

import com.cmartin.learn.domain.ProcessorModel._
import com.cmartin.learn.service.messaging.MyMessaging
import io.circe
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.ZIO

class ProcessorsSpec
  extends AnyFlatSpec
    with Matchers {

  val runtime = zio.Runtime.default

  it should "handle a list of processors" in {
    // given
    val filterEvent = FilterEvent("dummy filter event")
    val jsltEvent = JsltEvent("dummy jslt event")
    val restEvent = RestEvent("dummy rest event")

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


  it should "T2 encode" in {
    import GenericDerivation.encodeEvent
    import io.circe.syntax._

    val filterEvent: Event = FilterEvent("dummy filter event")
    info(filterEvent.asJson.noSpaces)

    val jsltEvent: Event = JsltEvent("dummy jslt event")
    info(jsltEvent.asJson.noSpaces)

    val restEvent: Event = RestEvent("dummy rest event")
    info(restEvent.asJson.noSpaces)
  }

  it should "T3 decode a filter event" in {
    import GenericDerivation.decodeEvent
    import io.circe.parser.decode

    val expectedEvent = FilterEvent("features.speed > 100")

    val eventEither: Either[circe.Error, Event] = decode[Event]("""{"type":"filter","predicate":"features.speed > 100"}""")

    eventEither.isRight shouldBe true
    info(eventEither.toString)

    eventEither shouldBe Right(expectedEvent)
  }


  def runProgram[T](p: ZIO[MyMessaging, Throwable, T]) =
    runtime
      .unsafeRun(
        p.provideLayer(MyMessaging.live)
      )

}
