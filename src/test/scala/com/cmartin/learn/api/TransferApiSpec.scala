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

  it should "T1 retrieve a Transfer via /transfers endpoint" in {
    // G I V E N
    Get(s"$API_TEXT/$API_VERSION/$TRANSFERS_TEXT/200") ~>
      // W H E N
      TransferApi.getRoute ~>
      // T H E N
      check {
        status shouldBe StatusCodes.OK
        header[`Content-Type`] shouldBe Some(contentTypeJson)
        parseTransfer(entityAs[String]) shouldBe Right(TransferEndpoint.transferExample)
        info(entityAs[String])
      }
  }

  it should "T2 get a not found via /transfers endpoint" in {
    // G I V E N
    Get(s"$API_TEXT/$API_VERSION/$TRANSFERS_TEXT/400") ~>
      // W H E N
      TransferApi.getRoute ~>
      // T H E N
      check {
        status shouldBe StatusCodes.BadRequest
        header[`Content-Type`] shouldBe Some(contentTypeJson)
        info(entityAs[String])
      }
  }

  it should "T3 get a not found via /transfers endpoint" in {
    // G I V E N
    Get(s"$API_TEXT/$API_VERSION/$TRANSFERS_TEXT/404") ~>
      // W H E N
      TransferApi.getRoute ~>
      // T H E N
      check {
        status shouldBe StatusCodes.NotFound
        header[`Content-Type`] shouldBe Some(contentTypeJson)
        info(entityAs[String])
      }
  }

  it should "T4 get a server error via /transfers endpoint" in {
    // G I V E N
    Get(s"$API_TEXT/$API_VERSION/$TRANSFERS_TEXT/500") ~>
      // W H E N
      TransferApi.getRoute ~>
      // T H E N
      check {
        status shouldBe StatusCodes.InternalServerError
        header[`Content-Type`] shouldBe Some(contentTypeJson)
        info(entityAs[String])
      }
  }

  it should "T5 get an unavailable service error via /transfers endpoint" in {
    // G I V E N
    Get(s"$API_TEXT/$API_VERSION/$TRANSFERS_TEXT/503") ~>
      // W H E N
      TransferApi.getRoute ~>
      // T H E N
      check {
        status shouldBe StatusCodes.ServiceUnavailable
        header[`Content-Type`] shouldBe Some(contentTypeJson)
        info(entityAs[String])
      }
  }

  it should "T6 get an unknown error via /transfers endpoint" in {
    // G I V E N
    Get(s"$API_TEXT/$API_VERSION/$TRANSFERS_TEXT/666") ~>
      // W H E N
      TransferApi.getRoute ~>
      // T H E N
      check {
        status shouldBe StatusCodes.BadRequest
        header[`Content-Type`] shouldBe Some(contentTypeJson)
        info(entityAs[String])
      }
  }

}

object TransferApiSpec extends TransferApiSpec {

  //  import io.circe.generic.auto._

  import ApiCodecs.transferDecoder
  import io.circe.parser._

  def parseTransfer(json: String): Either[circe.Error, Transfer] = {
    decode[Transfer](json)
  }

}