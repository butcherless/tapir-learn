package com.cmartin.learn.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.`Content-Type`
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.cmartin.learn.api.ActuatorApiSpec.contentTypeJson
import com.cmartin.learn.api.ApiModel.TransferDto
import com.cmartin.learn.domain.DomainModel.Transfer
import com.cmartin.learn.domain.{ApiConverters, DomainModel}
import io.circe
import io.circe.generic.auto._
import io.circe.syntax._
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

  it should "T7 create a transfer via /transfer endpoint" in {

    Post(s"$API_TEXT/$API_VERSION/$TRANSFERS_TEXT")
      .withEntity(TransferEndpoint.transferExample.asJson.noSpaces) ~>
      TransferApi.postRoute ~>
      // THEN
      check {
        status shouldBe StatusCodes.Created
        info(entityAs[String])
        io.circe.parser.decode[TransferDto](entityAs[String]) shouldBe Right(TransferEndpoint.transferExample)
      }
  }

  it should "T8 convert a transfer dto to a transfer" in {
    // Given

    // When
    val convertedTransfer = ApiConverters.apiToModel(TransferEndpoint.transferExample)

    // Then
    convertedTransfer shouldBe transfer
  }

  it should "T9 convert a transfer to a transfer dto" in {
    // Given

    // When
    val convertedTransfer = ApiConverters.modelToApi(transfer)

    // Then
    convertedTransfer shouldBe TransferEndpoint.transferExample
  }
}

object TransferApiSpec {

  import io.circe.parser._

  val transfer = Transfer(
    "ES11 0182 1111 2222 3333 4444",
    "ES99 2038 9999 8888 7777 6666",
    100.00,
    DomainModel.EUR,
    "Viaje a Tenerife"
  )

  def parseTransfer(json: String): Either[circe.Error, TransferDto] = {
    decode[TransferDto](json)
  }

}