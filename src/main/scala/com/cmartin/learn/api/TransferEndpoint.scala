package com.cmartin.learn.api

import com.cmartin.learn.api.ApiModel.Transfer
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir.json.circe._
import sttp.tapir.{Endpoint, _}

trait TransferEndpoint {
  //json encode/decode via circe.generic.auto
  lazy val getTransferEndpoint: Endpoint[Unit, StatusCode, Transfer, Nothing] =
    endpoint
      .get
      .in(CommonEndpoint.baseEndpointInput / "transfers")
      .name("get-transfer-endpoint")
      .description("Get Transfer endpoint")
      .out(jsonBody[Transfer].example(ApiModel.transferExample))
      .errorOut(statusCode)

}

object TransferEndpoint extends TransferEndpoint
