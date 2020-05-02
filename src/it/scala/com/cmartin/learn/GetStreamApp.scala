package com.cmartin.learn

import java.nio.ByteBuffer

import sttp.client._
import sttp.client.asynchttpclient.ziostreams._
import zio.stream._
import zio.{App, ZIO, _}

import scala.concurrent.duration.Duration

object GetStreamApp extends App {


  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {

    AsyncHttpClientZioStreamsBackend().flatMap { implicit backend =>
      val response: Task[Response[Either[String, Stream[Throwable, ByteBuffer]]]] =
        basicRequest
          .get(uri"http://127.0.0.1:8080/api/v1.0/tenants/848860983001616384/vertexes")
          .response(asStream[Stream[Throwable, ByteBuffer]])
          .readTimeout(Duration.Inf)
          .send()

      response
    }.fold(
      _ => 1,
      _ => 0
    )

  }
}
