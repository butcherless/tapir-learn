package com.cmartin.learn.domain

import cats.syntax.functor._
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import zio.Task

object ProcessorModel {

  sealed trait Event {
    val name: String
  }

  final case class FilterEvent(predicate: String) extends Event {
    override val name: String = "filter"
  }

  final case class JsltEvent(transform: String) extends Event {
    override val name: String = "jslt"
  }

  final case class RestEvent(getUrl: String) extends Event {
    override val name: String = "rest"
  }


  trait Processor[E <: Event] {
    def handle(): Task[String]
  }

  final class FilterProcessor(filterEvent: FilterEvent) extends Processor[Event] {
    override def handle(): Task[String] = Task.effect(filterEvent.predicate.toString)
  }

  object FilterProcessor {
    def apply(filterEvent: FilterEvent): FilterProcessor = new FilterProcessor(filterEvent)
  }

  final class JsltProcessor(jsltEvent: JsltEvent) extends Processor[Event] {
    override def handle(): Task[String] = Task.effect(jsltEvent.transform.toString)
  }

  object JsltProcessor {
    def apply(jsltEvent: JsltEvent): JsltProcessor = new JsltProcessor(jsltEvent)
  }

  final class RestProcessor(restEvent: RestEvent) extends Processor[Event] {
    override def handle(): Task[String] = Task.effect(restEvent.getUrl.toString)
  }

  object RestProcessor {
    def apply(restEvent: RestEvent): RestProcessor = new RestProcessor(restEvent)
  }

  object GenericDerivation {

    implicit val encodeEvent: Encoder[Event] = Encoder.instance {
      case filter@FilterEvent(_) => filter.asJson
      case jslt@JsltEvent(_) => jslt.asJson
      case rest@RestEvent(_) => rest.asJson
    }

    implicit val decodeEvent: Decoder[Event] =
      List[Decoder[Event]](
        Decoder[FilterEvent].widen,
        Decoder[JsltEvent].widen,
        Decoder[RestEvent].widen
      ).reduceLeft(_ or _)
  }

}
