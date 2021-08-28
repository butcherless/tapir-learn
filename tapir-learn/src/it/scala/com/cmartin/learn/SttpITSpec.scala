package com.cmartin.learn

import com.cmartin.learn.api.Model.BuildInfoDto
import com.cmartin.learn.api.Model.TransferDto
import com.cmartin.learn.api.TransferEndpoint
import io.circe
import io.circe.generic.auto._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sttp.capabilities.zio.ZioStreams
import sttp.client3._
import sttp.client3._
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend
import sttp.client3.asynchttpclient.zio.SttpClient
import sttp.client3.asynchttpclient.zio._
import sttp.client3.basicRequest
import sttp.client3.circe._
import sttp.model.StatusCode
import zio.App
import zio.UIO
import zio.ZIO
import zio._

class SttpITSpec extends AnyFlatSpec with Matchers {

  val runtime = zio.Runtime.default

  behavior of "REST API"

  it should "respond Ok status for a health GET request" in {
    val request =
      basicRequest
        .get(uri"http://localhost:8080/api/v1.0/health")
        .response(asJson[BuildInfoDto])

    val doGet =
      send(request)

    val layeredDoGet =
      doGet.provideCustomLayer(AsyncHttpClientZioBackend.layer())

    val response =
      runtime.unsafeRun(layeredDoGet)

    response.code shouldBe StatusCode.Ok
  }

  it should "respond Ok status for a get Transfer GET request" in {
    val id = 1
    val request =
      basicRequest
        .get(uri"http://localhost:8080/api/v1.0/transfers/$id")
        .response(asJson[TransferDto])

    val doGet = send(request)

    val layeredDoGet = doGet.provideCustomLayer(AsyncHttpClientZioBackend.layer())

    val response = runtime.unsafeRun(layeredDoGet)

    response.code shouldBe StatusCode.Ok
  }

  it should "respond NotFound status for a non-existent Transfer GET  request" in {
    val id = 404
    val request =
      basicRequest
        .get(uri"http://localhost:8080/api/v1.0/transfers/$id")
        .response(asJson[TransferDto])

    val doGet = send(request)

    val layeredDoGet = doGet.provideCustomLayer(AsyncHttpClientZioBackend.layer())

    val response = runtime.unsafeRun(layeredDoGet)

    response.code shouldBe StatusCode.NotFound
  }

  it should "respond InternalServerError status for a simulated error in a Transfer GET request" in {
    val id = 500
    val request =
      basicRequest
        .get(uri"http://localhost:8080/api/v1.0/transfers/$id")
        .response(asJson[TransferDto])

    val doGet = send(request)

    val layeredDoGet = doGet.provideCustomLayer(AsyncHttpClientZioBackend.layer())

    val response = runtime.unsafeRun(layeredDoGet)

    response.code shouldBe StatusCode.InternalServerError
  }

  it should "respond Created status for a Transfer POST request" in {
    val request =
      basicRequest
        .body(TransferEndpoint.transferExample)
        .post(uri"http://localhost:8080/api/v1.0/transfers/")

    val doPost       = send(request)
    val layeredDoGet = doPost.provideCustomLayer(AsyncHttpClientZioBackend.layer())

    val response = runtime.unsafeRun(layeredDoGet)

    response.code shouldBe StatusCode.Created
  }

  it should "respond BadRequest status for an invalid Transfer POST request" in {
    val request =
      basicRequest
        .body("""{ "key" : "invalid-transfer }""")
        .post(uri"http://localhost:8080/api/v1.0/transfers/")

    val doPost       = send(request)
    val layeredDoGet = doPost.provideCustomLayer(AsyncHttpClientZioBackend.layer())

    val response = runtime.unsafeRun(layeredDoGet)

    response.code shouldBe StatusCode.BadRequest
  }

  it should "respond Ok status for a Trasnfer GET request with map params" in {
    val paramMap = Map("sender" -> "ES11 0182 1111 2222 3333 4444")
    val request =
      basicRequest
        .get(uri"http://localhost:8080/api/v1.0/transfers?$paramMap")
        .response(asJson[TransferDto])

    val doGet = send(request)

    val layeredDoGet = doGet.provideCustomLayer(AsyncHttpClientZioBackend.layer())

    val response = runtime.unsafeRun(layeredDoGet)

    response.code shouldBe StatusCode.Ok

  }

  /*
  def sendRequest[T](request: RequestT[Identity, Either[ResponseError[circe.Error], T], Nothing]) =
    for {
      response <- send(request)
      _        <- UIO(info(s"response.code: ${response.code}"))
      _        <- UIO(info(s"response.body: ${response.body}"))
    } yield response

  def sendPostRequest[T](request: Request[Either[String, String], Nothing]) =
    for {
      response <- send(request)
      _        <- UIO(info(s"response.code: ${response.code}"))
      _        <- UIO(info(s"response.body: ${response.body}"))
    } yield response
   */
}
