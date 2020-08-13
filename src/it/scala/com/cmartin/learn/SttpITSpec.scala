package com.cmartin.learn

import com.cmartin.learn.api.ApiModel.{BuildInfoDto, TransferDto}
import com.cmartin.learn.api.TransferEndpoint
import com.cmartin.learn.domain.ApiConverters
import io.circe
import io.circe.generic.auto._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sttp.client.asynchttpclient.zio.{AsyncHttpClientZioBackend, SttpClient}
import sttp.client.circe._
import sttp.client.{basicRequest, _}
import sttp.model.{Method, StatusCode}
import zio.{UIO, ZIO}

class SttpITSpec extends AnyFlatSpec with Matchers {

  val runtime = zio.Runtime.default

  behavior of "REST API"

  //TODO move to unit tests
  it should "respond Ok status stub backend for health request" in {
    val dtoResponse: BuildInfoDto = ApiConverters.modelToApi()

    val backend = AsyncHttpClientZioBackend.stub
      .whenRequestMatches { req =>
        req.method == Method.GET && req.uri.path.last == "health"
      }
      .thenRespond(dtoResponse)

    val request =
      basicRequest
        .get(uri"http://localhost:8080/api/v1.0/health")
        .response(asJson[BuildInfoDto])

    val response: Response[Either[ResponseError[circe.Error], BuildInfoDto]] =
      runtime
        .unsafeRun(
          backend.send(request)
        )

    response.code shouldBe StatusCode.Ok
    response.body shouldBe dtoResponse
  }

  val backend = AsyncHttpClientZioBackend.stub
    .whenRequestMatches { req =>
      req.method == Method.GET && req.uri.path.contains("transfers") && req.uri.path.last == "1"
    }
    .thenRespond(TransferEndpoint.transferExample)
    .whenRequestMatches { req =>
      req.method == Method.GET && req.uri.path.contains("transfers") && req.uri.path.last == "400"
    }
    .thenRespond(Response("BAD_REQUEST", StatusCode.BadRequest))
    .whenRequestMatches { req =>
      req.method == Method.GET && req.uri.path.contains("transfers") && req.uri.path.last == "500"
    }
    .thenRespond(Response("SERVER_ERROR", StatusCode.InternalServerError))


  it should "TODO respond Ok for an existent transfer identifier" in {
    val request =
      basicRequest
        .get(uri"http://localhost:8080/api/v1.0/transfers/1")
        .response(asJson[TransferDto])

    val response = runtime.unsafeRun(backend.send(request))

    response.code shouldBe StatusCode.Ok
    response.body shouldBe TransferEndpoint.transferExample
  }

  it should "TODO respond Bad Request for an invalid request" in {
    val request =
      basicRequest
        .get(uri"http://localhost:8080/api/v1.0/transfers/400")
        .response(asJson[TransferDto])

    val response = runtime.unsafeRun(backend.send(request))

    response.code shouldBe StatusCode.BadRequest
    response.body.isLeft shouldBe true
  }

  it should "TODO respond Not Found for a missing transfer" in {
    val request =
      basicRequest
        .get(uri"http://localhost:8080/api/v1.0/transfers/404")
        .response(asJson[TransferDto])

    val response = runtime.unsafeRun(backend.send(request))

    response.code shouldBe StatusCode.NotFound
    response.body.isLeft shouldBe true
  }

  it should "TODO respond Server Error for a server failure" in {
    val request =
      basicRequest
        .get(uri"http://localhost:8080/api/v1.0/transfers/500")
        .response(asJson[TransferDto])

    val response = runtime.unsafeRun(backend.send(request))

    response.code shouldBe StatusCode.InternalServerError
    response.body.isLeft shouldBe true
  }

  //TODO end

  it should "respond Ok status for a health GET request" in {
    val request =
      basicRequest
        .get(uri"http://localhost:8080/api/v1.0/health")
        .response(asJson[BuildInfoDto])

    val doGet: ZIO[SttpClient, Throwable, Response[Either[ResponseError[circe.Error], BuildInfoDto]]] =
      sendRequest[BuildInfoDto](request)

    val layeredDoGet: ZIO[zio.ZEnv, Throwable, Response[Either[ResponseError[circe.Error], BuildInfoDto]]] =
      doGet.provideCustomLayer(AsyncHttpClientZioBackend.layer())

    val response: Response[Either[ResponseError[circe.Error], BuildInfoDto]] = runtime.unsafeRun(layeredDoGet)

    response.code shouldBe StatusCode.Ok
  }

  it should "respond Ok status for a get Transfer GET request" in {
    val id = 1
    val request =
      basicRequest
        .get(uri"http://localhost:8080/api/v1.0/transfers/$id")
        .response(asJson[TransferDto])

    val doGet = sendRequest[TransferDto](request)

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

    val doGet = sendRequest[TransferDto](request)

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

    val doGet = sendRequest[TransferDto](request)

    val layeredDoGet = doGet.provideCustomLayer(AsyncHttpClientZioBackend.layer())

    val response = runtime.unsafeRun(layeredDoGet)

    response.code shouldBe StatusCode.InternalServerError
  }

  it should "respond Created status for a Transfer POST request" in {
    val request: Request[Either[String, String], Nothing] =
      basicRequest
        .body(TransferEndpoint.transferExample)
        .post(uri"http://localhost:8080/api/v1.0/transfers/")

    val doPost       = sendPostRequest[TransferDto](request)
    val layeredDoGet = doPost.provideCustomLayer(AsyncHttpClientZioBackend.layer())

    val response = runtime.unsafeRun(layeredDoGet)

    response.code shouldBe StatusCode.Created
  }

  it should "respond BadRequest status for an invalid Transfer POST request" in {
    val request: Request[Either[String, String], Nothing] =
      basicRequest
        .body("""{ "key" : "invalid-transfer }""")
        .post(uri"http://localhost:8080/api/v1.0/transfers/")

    val doPost       = sendPostRequest[TransferDto](request)
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

    val doGet = sendRequest[TransferDto](request)

    val layeredDoGet = doGet.provideCustomLayer(AsyncHttpClientZioBackend.layer())

    val response = runtime.unsafeRun(layeredDoGet)

    response.code shouldBe StatusCode.Ok

  }

  def sendRequest[T](request: RequestT[Identity, Either[ResponseError[circe.Error], T], Nothing]) =
    for {
      response <- SttpClient.send(request)
      _        <- UIO(info(s"response.code: ${response.code}"))
      _        <- UIO(info(s"response.body: ${response.body}"))
    } yield response

  def sendPostRequest[T](request: Request[Either[String, String], Nothing]) =
    for {
      response <- SttpClient.send(request)
      _        <- UIO(info(s"response.code: ${response.code}"))
      _        <- UIO(info(s"response.body: ${response.body}"))
    } yield response

}