package com.cmartin.learn.api

import java.nio.charset.StandardCharsets

import com.cmartin.learn.api.ApiModel.{ACEntity, Output, Transfer}
import io.circe.generic.auto._
import io.circe.syntax._
import sttp.model.StatusCode
import sttp.tapir.Codec.JsonCodec
import sttp.tapir.DecodeResult.Value
import sttp.tapir.json.circe._
import sttp.tapir.{Endpoint, _}

trait TransferEndpoint {

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


  case class OutputHelper(value: String)

  implicit val jc = new JsonCodec[Output] {
    override def encode(t: Output): String = t match {
      case ApiModel.ComOut =>
        jsonPrinter.print(OutputHelper("ComOut").asJson)

      case ApiModel.ShaOut =>
        jsonPrinter.print(OutputHelper("ShaOutput").asJson)
    }

    override def rawDecode(s: String): DecodeResult[Output] = Value(ApiModel.ShaOut)

    override def meta: CodecMeta[Output, CodecFormat.Json, String] =
      CodecMeta(implicitly[Schema[Output]], CodecFormat.Json(), StringValueType(StandardCharsets.UTF_8), implicitly[Validator[Output]])
  }


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

  //json encode/decode via circe.generic.auto
  lazy val getTransferEndpoint: Endpoint[Unit, StatusCode, Transfer, Nothing] =
    endpoint
      .get
      .in(CommonEndpoint.baseEndpointInput / "transfers")
      .name("get-transfer-endpoint")
      .description("Get Transfer Endpoint")
      .out(jsonBody[Transfer].example(ApiModel.transferExample))
      .errorOut(statusCode)

  lazy val getACEntityEndpoint: Endpoint[Unit, StatusCode, ACEntity, Nothing] =
    endpoint
      .get
      .in(CommonEndpoint.baseEndpointInput / "acEntity")
      .name("get-acEntity-endpoint")
      .description("Get AC Entity Endpoint")
      .out(jsonBody[ACEntity].example(ApiModel.acEntityExample))
      .errorOut(statusCode)


  lazy val getComOutputEndpoint: Endpoint[Unit, StatusCode, Output, Nothing] =
    endpoint
      .get
      .in(CommonEndpoint.baseEndpointInput / "com-output")
      .name("get-com-output-endpoint")
      .description("Get Com Output Endpoint")
      .out(jsonBody[Output].example(ApiModel.ComOut))
      .errorOut(statusCode)

  lazy val getShaOutputEndpoint: Endpoint[Unit, StatusCode, Output, Nothing] =
    endpoint
      .get
      .in(CommonEndpoint.baseEndpointInput / "sha-output")
      .name("get-sha-out-endpoint")
      .description("Get Sha Output Endpoint")
      .out(jsonBody[Output].example(ApiModel.ShaOut))
      .errorOut(statusCode)

}

object TransferEndpoint extends TransferEndpoint
