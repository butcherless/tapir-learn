package com.cmartin.learn

import com.cmartin.learn.api.Model.{BuildInfoDto, TransferDto}
import com.cmartin.learn.api.TransferEndpoint
import io.circe.generic.auto._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sttp.capabilities.WebSockets
import sttp.capabilities.zio.ZioStreams
import sttp.client3._
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend
import sttp.client3.circe._
import sttp.model.StatusCode
import zio.Runtime.{default => runtime}
import zio.{Task, Unsafe, ZIO}

/** Live tests against a running server - start it first with `tapir-webapp/reStart`
  * (see README's "Integration" section), then run with `sbt integration/test`.
  * Not part of the default aggregate `compile`/`test`.
  */
class SttpITSpec
    extends AnyFlatSpec
    with Matchers
    with BeforeAndAfterAll {

  import SttpITSpec._

  private val baseUrl = "http://localhost:8080/api/v1.0"

  behavior of "REST API (live server)"

  it should "respond Ok status for a health GET request" in {
    val request =
      basicRequest
        .get(uri"$baseUrl/health")
        .response(asJson[BuildInfoDto])

    val response = unsafeRun(backend.send(request))

    response.code shouldBe StatusCode.Ok
    response.body.map(_.name) shouldBe Right("tapir-webapp")
  }

  it should "respond Ok status for an existent transfer GET request" in {
    val request =
      basicRequest
        .get(uri"$baseUrl/transfers/1")
        .response(asJson[TransferDto])

    val response = unsafeRun(backend.send(request))

    response.code shouldBe StatusCode.Ok
    response.body shouldBe Right(TransferEndpoint.transferExample)
  }

  it should "respond NotFound status for a non-existent transfer GET request" in {
    val response = unsafeRun(backend.send(basicRequest.get(uri"$baseUrl/transfers/404")))

    response.code shouldBe StatusCode.NotFound
  }

  it should "respond InternalServerError status for a simulated error GET request" in {
    val response = unsafeRun(backend.send(basicRequest.get(uri"$baseUrl/transfers/500")))

    response.code shouldBe StatusCode.InternalServerError
  }

  it should "respond Created status for a Transfer POST request" in {
    val request =
      basicRequest
        .body(TransferEndpoint.transferExample)
        .post(uri"$baseUrl/transfers")
        .response(asJson[TransferDto])

    val response = unsafeRun(backend.send(request))

    response.code shouldBe StatusCode.Created
    response.body shouldBe Right(TransferEndpoint.transferExample)
  }

  override def afterAll(): Unit =
    unsafeRun(backend.close())
}

object SttpITSpec {

  val backend: SttpBackend[Task, ZioStreams & WebSockets] =
    unsafeRun(AsyncHttpClientZioBackend())

  def unsafeRun[E, A](program: ZIO[Any, E, A]): A =
    Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(program).getOrThrowFiberFailure()
    }
}
