package com.cmartin.learn.api

import com.cmartin.learn.api.ApiModel._
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir.json.circe._
import sttp.tapir.{Endpoint, _}

trait TransferEndpoint
  extends ApiCodecs {

  import TransferEndpoint._

  /*
      E N D P O I N T S
   */

  //json encode/decode via circe.generic.auto
  lazy val getTransferEndpoint: Endpoint[Unit, StatusCode, Transfer, Nothing] =
    endpoint
      .get
      .in(CommonEndpoint.baseEndpointInput / TRANSFERS_TEXT)
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
      .out(jsonBody[ACEntity].example(acEntityExample))
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

object TransferEndpoint extends TransferEndpoint {
  val TRANSFERS_TEXT = "transfers"
  //  def buildCodec[T](decoder: Decoder[T], encoder: Encoder[T]): JsonCodec[T] =
  //    implicitly[JsonCodec[Json]].map(json => json.as[T](decoder) match {
  //      case Left(_) => throw new RuntimeException("ParsingError")
  //      case Right(value) => value
  //    })(obj => obj.asJson(encoder))
  //

  val acEntityExample: ACEntity =
    ACEntity(
      ComposedId(11111111L, 22222222L),
      Sids(
        Source(1111L, "src-filter", ComOut), Some(State(2222L, "sta-filter", ComOut, ShaStrategy,
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
