package com.cmartin.learn.api

import com.cmartin.learn.api.ApiModel.{ACEntity, Output, Transfer}
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir.Codec.JsonCodec
import sttp.tapir.DecodeResult.Value
import sttp.tapir.{Endpoint, _}
import sttp.tapir.json.circe._

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


  implicit val jc = new JsonCodec[Output] {
    override def encode(t: Output): String = "output2string"

    override def rawDecode(s: String): DecodeResult[Output] = Value(ApiModel.ShaOut)

    override def meta: CodecMeta[Output, CodecFormat.Json, String] = ???
  }

  //json encode/decode via circe.generic.auto
  lazy val getTransferEndpoint: Endpoint[Unit, StatusCode, Transfer, Nothing] =
    endpoint
      .get
      .in(CommonEndpoint.baseEndpointInput / "transfers")
      .name("get-transfer-endpoint")
      .description("Get Transfer Endpoint")
      .out(jsonBody[Transfer].example(ApiModel.transferExample))
      .errorOut(statusCode)

//  lazy val getACEntityEndpoint: Endpoint[Unit, StatusCode, ACEntity, Nothing] =
  lazy val getACEntityEndpoint: Endpoint[Unit, StatusCode, Output, Nothing] =
    endpoint
      .get
      .in(CommonEndpoint.baseEndpointInput / "nestedEntities")
      .name("get-nested-entities-endpoint")
      .description("Get Nested Entities Endpoint")
      .out(jsonBody[Output].example(ApiModel.ShaOut))
      .errorOut(statusCode)

}

object TransferEndpoint extends TransferEndpoint
