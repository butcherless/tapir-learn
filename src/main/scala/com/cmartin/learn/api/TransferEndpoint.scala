package com.cmartin.learn.api

import com.cmartin.learn.api.ApiModel._
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir.EndpointOutput.StatusMapping
import sttp.tapir.json.circe._
import sttp.tapir.{Endpoint, _}

trait TransferEndpoint
  extends ApiCodecs {

  import TransferEndpoint._

  /*
      E N D P O I N T S
   */

  val transferIdPath = path[TransferId]("transferId")
  val breMapping: StatusMapping[BadRequestError] = statusMapping(StatusCode.BadRequest, jsonBody[BadRequestError])
  val nfeMapping: StatusMapping[NotFoundError] = statusMapping(StatusCode.NotFound, jsonBody[NotFoundError])
  val iseMapping: StatusMapping[ServerError] = statusMapping(StatusCode.InternalServerError, jsonBody[ServerError])
  val sueMapping: StatusMapping[ServiceUnavailableError] = statusMapping(StatusCode.ServiceUnavailable, jsonBody[ServiceUnavailableError])
  val deMapping = statusDefaultMapping(jsonBody[UnknownError])

  //json encode/decode via circe.generic.auto
  lazy val getTransferEndpoint: Endpoint[TransferId, ErrorInfo, TransferDto, Nothing] =
    endpoint
      .get
      .name("get-transfer-endpoint")
      .description("Retrieve Transfer Endpoint")
      .in(CommonEndpoint.baseEndpointInput / TRANSFERS_TEXT)
      .in(transferIdPath)
      .out(jsonBody[TransferDto].example(transferExample))
      .errorOut(
        oneOf[ErrorInfo](breMapping, nfeMapping, iseMapping, sueMapping, deMapping)
      )


  lazy val postTransferEndpoint: Endpoint[TransferDto, StatusCode, TransferDto, Nothing] =
    endpoint
      .post
      .name("post-transfer-endpoint")
      .description(("Create Transfer Endpoint"))
      .in(CommonEndpoint.baseEndpointInput / TRANSFERS_TEXT)
      .in(jsonBody[TransferDto].example(transferExample))
      .out(statusCode(StatusCode.Created).and(jsonBody[TransferDto].example(transferExample)))
      .errorOut(statusCode)


  lazy val getACEntityEndpoint: Endpoint[Unit, StatusCode, ACEntity, Nothing] =
    endpoint
      .get
      .name("get-acEntity-endpoint")
      .description("Get AC Entity Endpoint")
      .in(CommonEndpoint.baseEndpointInput / "acEntity")
      .out(jsonBody[ACEntity].example(acEntityExample))
      .errorOut(statusCode)


  lazy val getComOutputEndpoint: Endpoint[Unit, StatusCode, Output, Nothing] =
    endpoint
      .get
      .name("get-com-output-endpoint")
      .description("Get Com Output Endpoint")
      .in(CommonEndpoint.baseEndpointInput / "com-output")
      .out(jsonBody[Output].example(ApiModel.ComOut))
      .errorOut(statusCode)

  lazy val getShaOutputEndpoint: Endpoint[Unit, StatusCode, Output, Nothing] =
    endpoint
      .get
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


  val acEntityExample: ACEntity =
    ACEntity(
      ComposedId(11111111L, 22222222L),
      Sids(
        Source(1111L, "src-filter", Merge, ComOut),
        Some(State(2222L, "sta-filter", None, ComOut, ShaStrategy,
          Processors(Seq("in1", "in2"), Seq("ex1", "ex2"), Seq("t1", "t2", "t3"))))),
      ComOut
    )
}


/*
    R E S E A R C H
 */

//  def decode(s: String): DecodeResult[Output] = s match {
//    case "com" => DecodeResult.Value(ApiModel.ShaOut)
//    case _ => DecodeResult.Error(s, new RuntimeException("Non an element of ADT"))
//  }
//  def encode(output: Output): String = "TODO: Output type serialization implementation"

//implicit val x: JsonCodec[Output] = encoderDecoderCodec[Output]


//  implicit val myIdCodec: Codec[Output, Json, String] = Codec.stringPlainCodecUtf8
//    .mapDecode(decode)(encode)

// or, using the type alias for codecs in the TextPlain format and String as the raw value:
//  implicit val myIdCodec: JsonCodec[Output] = Codec.stringPlainCodecUtf8
//    .mapDecode(decode)(encode)


/*
  implicit def plainCodecForColor: PlainCodec[Output] = {
    Codec.stringPlainCodecUtf8
      .map[Output]({
        case "com"  => ComOut
        case "sha" => ShaOut
      })(_.toString.toLowerCase)
      .validate(Validator.enum)
  }

  implicit def jcodec : JsonCodec[Output] = {
    Codec.stringPlainCodecUtf8
  }

  implicit def colorValidator: Validator[Output] = Validator.enum.encode(_.toString.toLowerCase)
*/


//  case class OutputHelper(value: String)
//  implicit val jc = new JsonCodec[Output] {
//    override def encode(t: Output): String = t match {
//      case ApiModel.ComOut =>
//        jsonPrinter.print(OutputHelper("ComOut").asJson)
//
//      case ApiModel.ShaOut =>
//        jsonPrinter.print(OutputHelper("ShaOutput").asJson)
//    }
//
//    override def rawDecode(s: String): DecodeResult[Output] = Value(ApiModel.ShaOut)
//
//    override def meta: CodecMeta[Output, CodecFormat.Json, String] =
//      CodecMeta(implicitly[Schema[Output]], CodecFormat.Json(), StringValueType(StandardCharsets.UTF_8), implicitly[Validator[Output]])
//  }
