package com.cmartin.learn.service

import com.cmartin.learn.domain.ProcessorModel
import com.cmartin.learn.domain.ProcessorModel.GenericDerivation.{eventDecoder, eventEncoder}
import com.cmartin.learn.domain.ProcessorModel._
import com.cmartin.learn.service.messaging.MyMessaging
import io.circe
import io.circe.Json
import io.circe.parser.{decode, parse}
import io.circe.syntax._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.{Task, ZIO}

class ProcessorsSpec extends AnyFlatSpec with Matchers {

  import ProcessorsSpec._

  val runtime = zio.Runtime.default

  behavior of "Processor Sequence"

  it should "A1 handle a list of processors" in {
    // given
    val filterEvent = FilterDefinition("dummy filter event")
    val jsltEvent   = JsltDefinition("dummy jslt event")
    //val restEvent   = RestEvent("dummy rest event")

    val processors: Seq[Processor[ProcessorDefinition]] =
      Seq(
        FilterProcessor(filterEvent)
        //JsltProcessor(jsltEvent)
        //  RestProcessor(restEvent)
      )

    // when
    val p = for {
      rs <- MyMessaging.handle(processors)
    } yield rs

    // then
    val result: Seq[String] = runProgram(p)

    //result shouldBe Seq("dummy filter event", "dummy jslt event", "dummy rest event")
  }

  it should "B1 encode a filter event, object -> json" in {
    val feJson = filterDefinition.asJson
    feJson shouldBe parse(filterDefinitionJson).getOrElse(Json.Null)
  }

  it should "B2 encode a jslt event, object -> json" in {
    val jeJson = jsltDefinition.asJson
    jeJson shouldBe parse(jsltDefinitionJson).getOrElse(Json.Null)
  }

  it should "B3 encode a rest event, object -> json" in {
    val reJson = restDefinition.asJson
    reJson shouldBe parse(restDefinitionJson).getOrElse(Json.Null)
  }

  it should "B4 encode all Events, object -> json" in {
    val events = Seq(filterDefinition, jsltDefinition, restDefinition).asJson
    events shouldBe parse(processorDefinitionsJson).getOrElse(Json.Null)
  }

  it should "C1 decode a filter event, json -> object" in {
    val fEvent: ProcessorDefinition = decodeProcessor(filterDefinitionJson)
    fEvent shouldBe filterDefinition
  }

  it should "C2 decode a jslt event, json -> object" in {
    val jEvent: ProcessorDefinition = decodeProcessor(jsltDefinitionJson)
    jEvent shouldBe jsltDefinition
  }

  it should "C3 decode a rest event, json -> object" in {
    val rEvent: ProcessorDefinition = decodeProcessor(restDefinitionJson)
    rEvent shouldBe restDefinition
  }

  it should "C4 decode a filter event with missing properties, json -> object" in {
    val fEvent: ProcessorDefinition = decodeProcessor(filterDefinitionMissingPropsJson)
    fEvent shouldBe filterDefinitionMissingProps
  }

  it should "C5 decode all Events" in {
    val processors: Seq[ProcessorDefinition] = decodeProcessors(processorDefinitionsJson)

    processors shouldBe Seq(filterDefinition, jsltDefinition, restDefinition)
  }

  ignore should "D1 parse a sequence of processors" in {
    val parsedProcessors = parse(processorsJson)

    parsedProcessors.isRight shouldBe true
    parsedProcessors map { json =>
      info(json.asArray.toString())
    //TODO json.asArray shouldBe  Vector(filterEvent.asJson, jsltEvent.asJson, restEvent.asJson)
    }
  }

  it should "E1" in {
    val p1: FilterProcessor            = FilterProcessor(FilterDefinition("features.speed > 100"))
    val p2: JsltProcessor              = JsltProcessor(JsltDefinition("{ * : . }", "key1"))
    val processors: Seq[BaseProcessor] = Seq(p1, p2)

    val p: Task[String] = ProcessorModel.processList(processors, """{ "key" : "initial-message" }""")

    val r: String = runtime.unsafeRun(p)

    info(r)
  }

  def decodeProcessor(eventString: String): ProcessorDefinition =
    decode[ProcessorDefinition](eventString)
      .fold(
        e => fail(e.getMessage),
        identity
      )

  def decodeProcessors(eventsString: String): Seq[ProcessorDefinition] =
    decode[Seq[ProcessorDefinition]](eventsString)
      .fold(
        e => fail(e.getMessage),
        identity
      )

  def eitherToProcessor[T](eventEither: Either[circe.Error, T]): T =
    eventEither.fold(e => fail(e.getMessage), identity)

  def runProgram[T](p: ZIO[MyMessaging, Throwable, T]) =
    runtime
      .unsafeRun(
        p.provideLayer(MyMessaging.live)
      )

}

object ProcessorsSpec {
  val filterDefinitionMissingProps: ProcessorDefinition = FilterDefinition("features.speed > 100")
  val filterDefinitionMissingPropsJson =
    """
      |{
      |  "name": "filter",
      |  "predicate": "features.speed > 100"
      |}
      |""".stripMargin

  val filterDefinition: ProcessorDefinition = FilterDefinition("features.speed > 100", "key1.key2", "key3", "key4")
  val filterDefinitionJson =
    """
      |{
      |  "name": "filter",
      |  "predicate": "features.speed > 100",
      |  "inputPath": "key1.key2",
      |  "resultPath": "key3",
      |  "outputPath": "key4"
      |}
      |""".stripMargin

  val jsltDefinition: ProcessorDefinition = JsltDefinition("dummy-transform", "key1.key2", "key3", "key4")
  val jsltDefinitionJson =
    """
      |{
      |  "name": "jslt",
      |  "transform": "dummy-transform",
      |  "inputPath": "key1.key2",
      |  "resultPath": "key3",
      |  "outputPath": "key4"
      |}
      |""".stripMargin

  val restDefinition: ProcessorDefinition =
    RestDefinition("get", "http://localhost:8080/health", "key1.key2", "key3", "key4")
  val restDefinitionJson =
    """
      |{
      |  "name": "rest",
      |  "method": "get",
      |  "url": "http://localhost:8080/health",
      |  "inputPath": "key1.key2",
      |  "resultPath": "key3",
      |  "outputPath": "key4"
      |}
      |""".stripMargin

  val processorDefinitionsJson =
    s"""
       |[
       |$filterDefinitionJson,
       |$jsltDefinitionJson,
       |$restDefinitionJson
       |]
       |""".stripMargin

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
