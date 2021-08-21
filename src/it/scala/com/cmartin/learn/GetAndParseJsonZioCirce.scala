package com.cmartin.learn

import com.cmartin.learn.api.Model.ApiBuildInfo
import io.circe.generic.auto._
import sttp.client3._
import sttp.client3.asynchttpclient.zio._
import sttp.client3.circe._
import zio._
import zio.Console._

object GetAndParseJsonZioCirce extends App {
  override def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] = {

    case class HttpBinResponse(origin: String, headers: Map[String, String])

    val request =
      basicRequest.
      //get(uri"https://httpbin.org/get")
      //.response(asJson[HttpBinResponse])
      get(uri"http://localhost:8080/api/v1.0/health")
        .response(asJson[ApiBuildInfo])

    val program = for {
      response <- send(request)
      _ <- printLine(s"response code: ${response.code}")
      _ <- printLine(response.body.toString)
    } yield ()

    val schedulePolicy =
      Schedule
        .exponential(50.milliseconds) *>
        Schedule
          .recurs(5)
    //.tapOutput(i => console.putStrLn(s"$i")) , use logger

    program
      .retry(schedulePolicy)
      .provideCustomLayer(AsyncHttpClientZioBackend.layer())
      .foldM( // error management
        e => /*console.putStrLn(e.getMessage) *>*/ UIO(ExitCode.failure),
        _ => UIO(ExitCode.success)
      )
  }
}
