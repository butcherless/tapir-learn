package com.cmartin.learn.service

import java.util.UUID

import com.cmartin.learn.domain.ProcessorModel._
import zio.{Has, Layer, Task, ZIO, ZLayer}

package object messaging {
  type MyMessaging = Has[MyMessaging.Service]

  object MyMessaging {

    trait Service {
      def receive(topic: String): Task[String]

      def send(message: String, topic: String): Task[String]

      def handle(processors: Seq[Processor[ProcessorDefinition]]): Task[Seq[String]]
    }

    val live: Layer[Nothing, Has[MyMessaging.Service]] =
      ZLayer.succeed(
        new Service {
          override def receive(topic: String): Task[String] =
            Task.effect(
              s"receive message: ${UUID.randomUUID()} from topic: $topic"
            )

          override def send(message: String, topic: String): Task[String] =
            Task.effect(s"send message: $message -> to topic: $topic")

          override def handle(processors: Seq[Processor[ProcessorDefinition]]): Task[Seq[String]] =
            ZIO.foreach(processors)(_.doTask("dummy-message"))
        }
      )

    def receive(topic: String): ZIO[MyMessaging, Throwable, String] =
      ZIO.accessM(_.get.receive(topic))

    def send(message: String, topic: String): ZIO[MyMessaging, Throwable, String] =
      ZIO.accessM(_.get.send(message, topic))

    def handle(processors: Seq[Processor[ProcessorDefinition]]): ZIO[MyMessaging, Throwable, Seq[String]] =
      ZIO.accessM(_.get.handle(processors))
  }

}
