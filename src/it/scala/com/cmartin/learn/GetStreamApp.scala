package com.cmartin.learn

import sttp.client._
import sttp.client.asynchttpclient.zio._
import zio.stream._
import zio.{App, ZIO, _}

import scala.concurrent.duration.Duration

object GetStreamApp extends App {

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] = {

    val request =
      basicRequest
        .get(uri"http://127.0.0.1:8080/api/v1.0/tenants/848860983001616384/vertexes")
        .response(asStream[Stream[Throwable, Byte]])
        .readTimeout(Duration.Inf)

    val response: ZIO[SttpClient, Throwable, Response[Either[String, Stream[Throwable, Byte]]]] =
      SttpClient.send(request)

    //TODO
    val program: ZIO[SttpClient, Throwable, Long] = response.flatMap { either =>
      either.body
        .fold(
          e => Task.fail(new RuntimeException(e)),
          s => s.runCount
        )
    }

    UIO.succeed(ExitCode.success)
  }
}
