package com.cmartin.learn

import sttp.capabilities.WebSockets
import sttp.capabilities.zio.ZioStreams
import sttp.client3._
import sttp.client3.asynchttpclient.zio.{AsyncHttpClientZioBackend, SttpClient}
import sttp.model.StatusCode
import zio.Console._
import zio.Random._
import zio.Runtime.{default => runtime}
import zio._

object HttpClientTestApp
    extends ZIOAppDefault {

  val healthEndpoint = "http://localhost:8080/api/v1.0/health"
  val fiberCount = 20
  val loopCount = 500

  val req1 = basicRequest
    .body("Hello, world!")
    .post(uri"https://httpbin.org/post?hello=world")
  /*
  val b1: Task[SttpBackend[Task, ZioStreams with WebSockets]] =
    AsyncHttpClientZioBackend()

  val b2: Layer[Throwable, SttpClient] = AsyncHttpClientZioBackend.layer()

  val l1: ULayer[Task[SttpBackend[Task, ZioStreams with WebSockets]]] =
    ZLayer.succeed(b1)

  val res1: ZIO[Any, Throwable, Response[Either[String, String]]] =
    b1.flatMap(b =>
      b.send(req1)
    )

  val res2 = for {
    b <- ZIO.service[AsyncHttpClientZioBackend]
    resp <- b.send(req1)
  } yield resp

  val result = runtime.unsafeRun(res2.provide(b2))

  // http client backend
  implicit val backend: SttpBackend[Task, ZioStreams with WebSockets] =
    runtime.unsafeRun(AsyncHttpClientZioBackend())
  val urls: Seq[String] = List.fill(fiberCount)(healthEndpoint)
  val program1 = for {
    number <- nextIntBetween(500, 2000)
    delay <- ZIO.succeed(number)
    _ <- ZIO.sleep(delay.milliseconds)
    _ <- ZIO.foreachPar(urls)(doGet).withParallelism(fiberCount)
      .repeat(Schedule.recurs(loopCount - 1))
  } yield ()

  // Dummy method
  def makeGet(uri: String) =
    for {
      number <- nextIntBetween(500, 2000)
      delay <- ZIO.succeed(number)
      _ <- ZIO.sleep(delay.milliseconds)
      _ <- ZIO.succeed(println(s"sleep[$delay]: $uri"))
    } yield ()

  def doGet(endpoint: String) =
    for {
      response <- basicRequest.get(uri"$endpoint").send(backend)
      _ <- printLine(checkResponse(response))
    } yield ()

  def checkResponse(response: Response[Either[String, String]]): String =
    response.code match {
      case StatusCode.Ok => "Response Ok"
      case _             => "Response Error"
    }
   */
  val scopedBackend = AsyncHttpClientZioBackend()

  val backendLayer: ULayer[AsyncHttpClientZioBackend] = ???
  // ZLayer.succeed( AsyncHttpClientZioBackend())

  val requestBase64 = basicRequest
    .get(uri"https://httpbin.org/base64/dGhpcyBpcyBhIHN0dHAgcmVxdWVzdA%3D%3D")
  val program =
    for {
      // b <- ZIO.service[AsyncHttpClientZioBackend]
      resp <- scopedBackend.flatMap(_.send(requestBase64))
      // resp <- b.send(requestBase64)
      _ <- ZIO.logInfo(s"http status code: ${resp.code}")
    } yield ()

  // main function, needs exit = 0 [OK] or exit > 0 [ERROR]
  // Here the interpreter runs the program and performs side-effects
  def run = {
    program.exitCode
      .catchAllCause(cause => printLine(s"${cause.prettyPrint}").exitCode)
  }
}
