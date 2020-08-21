package com.cmartin.learn.api

import com.cmartin.learn.api.ApiModel._
import io.circe.Json
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir.EndpointOutput.StatusMapping
import sttp.tapir.json.circe._
import sttp.tapir.{Endpoint, _}

trait TransferEndpoint extends ApiCodecs {

  import TransferEndpoint._

  /*
      E N D P O I N T S
   */

  val transferIdPath                             = path[TransferId]("transferId")
  val breMapping: StatusMapping[BadRequestError] = statusMapping(StatusCode.BadRequest, jsonBody[BadRequestError])
  val nfeMapping: StatusMapping[NotFoundError]   = statusMapping(StatusCode.NotFound, jsonBody[NotFoundError])
  val iseMapping: StatusMapping[ServerError]     = statusMapping(StatusCode.InternalServerError, jsonBody[ServerError])
  val sueMapping: StatusMapping[ServiceUnavailableError] =
    statusMapping(StatusCode.ServiceUnavailable, jsonBody[ServiceUnavailableError])
  val deMapping = statusDefaultMapping(jsonBody[UnknownError])

  //json encode/decode via circe.generic.auto
  lazy val getTransferEndpoint: Endpoint[TransferId, ErrorInfo, TransferDto, Nothing] =
    endpoint.get
      .name("get-transfer-endpoint")
      .description("Retrieve Transfer Endpoint")
      .in(CommonEndpoint.baseEndpointInput / TRANSFERS_TEXT)
      .in(transferIdPath)
      .out(jsonBody[TransferDto].example(transferExample))
      .errorOut(
        oneOf[ErrorInfo](breMapping, nfeMapping, iseMapping, sueMapping, deMapping)
      )

  lazy val getFilteredTransferEndpoint =
    endpoint.get
      .name("get-filtered-transfer-endpoint")
      .description("Retrieve Transfer List Endpoint")
      .in(CommonEndpoint.baseEndpointInput / TRANSFERS_TEXT)
      .in(query[Option[String]]("sender").example(Some("ES11 0182 1111 2222 3333 4444")))
      .out(jsonBody[List[TransferDto]].example(transferListExample))
      .errorOut(
        oneOf[ErrorInfo](breMapping, nfeMapping, iseMapping, sueMapping, deMapping)
      )

  lazy val getWithHeaderTransferEndpoint: Endpoint[(TransferId, Int), ErrorInfo, Unit, Nothing] =
    endpoint.get
      .name("get-transfer-with-header-endpoint")
      .description("Retrieve Transfer with Header Endpoint")
      .in(CommonEndpoint.baseEndpointInput / s"$TRANSFERS_TEXT-with-header")
      .in(transferIdPath)
      .in(header[Int]("silent").example(1))
      .out(statusCode(StatusCode.NoContent))
      .errorOut(
        oneOf[ErrorInfo](breMapping, nfeMapping, iseMapping, sueMapping, deMapping)
      )

  /*
    private[api] lazy val assetsResourcePagination: EndpointInput[(Option[Long], Option[Int])] =
    query[Option[Long]]("offset").and(query[Option[Int]]("limit"))

   */

  lazy val postTransferEndpoint: Endpoint[TransferDto, StatusCode, TransferDto, Nothing] =
    endpoint.post
      .name("post-transfer-endpoint")
      .description(("Create Transfer Endpoint"))
      .in(CommonEndpoint.baseEndpointInput / TRANSFERS_TEXT)
      .in(jsonBody[TransferDto].example(transferExample))
      .out(
        statusCode(StatusCode.Created)
          .and(jsonBody[TransferDto].example(transferExample))
      )
      .errorOut(statusCode)

  lazy val postJsonEndpoint: Endpoint[Json, StatusCode, Json, Nothing] =
    endpoint.post
      .in(CommonEndpoint.baseEndpointInput / "bananas")
      .in(jsonBody[Json].example(jsonExample))
      .out(
        statusCode(StatusCode.Created)
          .and(jsonBody[Json].example(jsonExample))
      )
      .errorOut(statusCode)

  lazy val getACEntityEndpoint: Endpoint[Unit, StatusCode, ACEntity, Nothing] =
    endpoint.get
      .name("get-acEntity-endpoint")
      .description("Get AC Entity Endpoint")
      .in(CommonEndpoint.baseEndpointInput / "acEntity")
      .out(jsonBody[ACEntity].example(acEntityExample))
      .errorOut(statusCode)

  lazy val getComOutputEndpoint: Endpoint[Unit, StatusCode, Output, Nothing] =
    endpoint.get
      .name("get-com-output-endpoint")
      .description("Get Com Output Endpoint")
      .in(CommonEndpoint.baseEndpointInput / "com-output")
      .out(jsonBody[Output].example(ApiModel.ComOut))
      .errorOut(statusCode)

  lazy val getShaOutputEndpoint: Endpoint[Unit, StatusCode, Output, Nothing] =
    endpoint.get
      .name("get-sha-out-endpoint")
      .description("Get Sha Output Endpoint")
      .in(CommonEndpoint.baseEndpointInput / "sha-output")
      .out(jsonBody[Output].example(ApiModel.ShaOut))
      .errorOut(statusCode)

}

object TransferEndpoint extends TransferEndpoint {
  val TRANSFERS_TEXT = "transfers"
  //  def buildCodec[T](decoder: Decoder[T], encoder: Encoder[T]): JsonCodec[T] =
  //    implicitly[JsonCodec[Json]].map(json => json.as[T](decoder) match {
  //      case Left(_) => throw new RuntimeException("ParsingError")
  //      case Right(value) => value
  //    })(obj => obj.asJson(encoder))
  //

  val transferExample =
    TransferDto(
      "ES11 0182 1111 2222 3333 4444",
      "ES99 2038 9999 8888 7777 6666",
      100.00,
      "EUR",
      "Viaje a Tenerife"
    )

  val transfer2Example =
    TransferDto(
      "ES11 0182 1111 2222 3333 4444",
      "ES99 2095 3333 4444 2222 6666",
      250.00,
      "EUR",
      "Compra smartphone"
    )

  val transferListExample:List[TransferDto] = List(transferExample,transfer2Example)

  val acEntityExample: ACEntity =
    ACEntity(
      ComposedId(11111111L, 22222222L),
      Sids(
        Source(1111L, "src-filter", Merge, ComOut),
        Some(
          State(
            2222L,
            "sta-filter",
            None,
            ComOut,
            ShaStrategy,
            Processors(Seq("in1", "in2"), Seq("ex1", "ex2"), Seq("t1", "t2", "t3"))
          )
        )
      ),
      ComOut
    )

  val jsonStringExample = """{"id":1234}"""
  val jsonExample       = io.circe.parser.parse(jsonStringExample).getOrElse(Json.Null)

}
