package com.cmartin.learn

import sttp.client._
import sttp.client.asynchttpclient.zio.AsyncHttpClientZioBackend
import sttp.model.StatusCode
import zio.clock.Clock
import zio.console._
import zio.duration._
import zio.random._
import zio.{App, ExitCode, Schedule, Task, UIO, ZIO}

object HttpClientTestApp extends App {

  val healthEndpoint = "http://localhost:8080/api/v1.0/health"
  val fiberCount     = 20
  val loopCount      = 500

  // http client backend
  implicit val backend: SttpBackend[Task, Nothing, Nothing] =
    unsafeRun(AsyncHttpClientZioBackend())
  val urls: Seq[String] = List.fill(fiberCount)(healthEndpoint)

  // Dummy method
  def makeGet(uri: String): ZIO[Clock with Random, Nothing, Unit] =
    for {
      number <- nextIntBetween(500, 2000)
      delay  <- UIO(number)
      _      <- ZIO.sleep(delay.milliseconds)
      _      <- UIO(println(s"sleep[$delay]: $uri"))
    } yield ()

  val program: ZIO[Clock with zio.ZEnv, Throwable, Unit] = for {
    number <- nextIntBetween(500, 2000)
    delay  <- UIO(number)
    _      <- ZIO.sleep(delay.milliseconds)
    _      <- ZIO.foreachParN(fiberCount)(urls)(doGet) repeat Schedule.recurs(loopCount - 1)
  } yield ()

  def checkResponse(response: Response[Either[String, String]]): String =
    response.code match {
      case StatusCode.Ok => "Response Ok"
      case _             => "Response Error"
    }

  def doGet(endpoint: String): ZIO[Console, Throwable, Unit] =
    for {
      response <- basicRequest.get(uri"$endpoint").send()
      _        <- putStrLn(checkResponse(response))
    } yield ()

  // main function, needs exit = 0 [OK] or exit > 0 [ERROR]
  // Here the interpreter runs the program and performs side-effects
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] = {
    program.exitCode
      .catchAllCause(cause => putStrLn(s"${cause.prettyPrint}").exitCode)
  }
}
