package com.cmartin.learn

import sttp.client._
import sttp.client.asynchttpclient.zio.AsyncHttpClientZioBackend
import sttp.model.StatusCode
import zio.clock.Clock
import zio.console._
import zio.duration._
import zio.random._
import zio.{App, Schedule, Task, UIO, ZIO}

object HttpClientTestApp extends App {

  val healthEndpoint = "http://localhost:8080/api/v1.0/health"
  val fiberCount = 20
  val loopCount = 500

  // http client backend
  implicit val backend: SttpBackend[Task, Nothing, Nothing] =
    unsafeRun(AsyncHttpClientZioBackend())
  val urls = List.fill(fiberCount)(healthEndpoint)

  // Ddummy method
  def makeGet(uri: String) = for {
    number <- nextInt(1500)
    delay <- UIO(number + 500)
    _ <- ZIO.sleep(delay.milliseconds)
    _ <- UIO(println(s"sleep[$delay]: $uri"))
  } yield ()

  val program: ZIO[Clock with zio.ZEnv, Throwable, Unit] = for {
    number <- nextInt(1500)
    delay <- UIO(number + 500)
    _ <- ZIO.sleep(delay.milliseconds)
    _ <- ZIO.foreachParN(fiberCount)(urls)(doGet) repeat Schedule.recurs(loopCount - 1)
  } yield ()

  def checkResponse(response: Response[Either[String, String]]) = response.code match {
    case StatusCode.Ok => "Response Ok"
    case _ => "Response Error"
  }

  def doGet(endpoint: String) =
    for {
      response <- basicRequest.get(uri"$endpoint").send()
      _ <- UIO(checkResponse(response))
    } yield ()

  // main function, needs exit = 0 [OK] or exit > 0 [ERROR]
  // Here the interpreter runs the program and performs side-effects
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    (program as 0)
      .catchAllCause(cause => putStrLn(s"${cause.prettyPrint}") as 1)
  }
}
