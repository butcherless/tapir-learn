package com.cmartin.learn.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.`Content-Type`
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.cmartin.learn.api.ActuatorApiSpec.contentTypeJson
import com.cmartin.learn.api.ApiModel.Transfer
import io.circe
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TransferApiSpec
  extends AnyFlatSpec
    with Matchers
    with ScalatestRouteTest {

  import CommonEndpoint._
  import TransferApiSpec._
  import TransferEndpoint._

  behavior of "Transfer API"


  it should "retrieve a Transfer via /transfers endpoint" in {
    // W H E N
    Get(s"$API_TEXT/$API_VERSION/$TRANSFERS_TEXT") ~>
      TransferApi.routes
    // T H E N
    check {
      status shouldBe StatusCodes.OK
      header[`Content-Type`] shouldBe Some(contentTypeJson)
      parseTransfer(entityAs[String]) shouldBe ApiModel.transferExample //TODO move to Endpoint
    }
  }

}

object TransferApiSpec extends TransferApiSpec {

  import io.circe.generic.auto._
  import io.circe.parser._

  def parseTransfer(json: String): Either[circe.Error, Transfer] = {
    decode[Transfer](json)
  }

}