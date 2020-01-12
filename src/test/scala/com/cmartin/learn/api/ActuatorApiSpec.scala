package com.cmartin.learn.api

import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.`Content-Type`
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.cmartin.learn.api.ApiModel.BuildInfo
import io.circe
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

final class ActuatorApiSpec
  extends AnyFlatSpec
    with Matchers
    with ScalatestRouteTest {

  import ActuatorApiSpec._
  import CommonEndpoint._

  behavior of "Actuator API"

  it should "retrieve build information via /health endpoint" in {
    // W H E N
    Get(s"$API_TEXT/$API_VERSION/health") ~>
      ActuatorApi.route
    // T H E N
    check {
      status shouldBe StatusCodes.OK
      header[`Content-Type`] shouldBe Some(contentTypeJson)
      parseBuildInfo(entityAs[String]).map(
        info => info.version == ApiModel.APP_VERSION && info.appName == ApiModel.APP_NAME)
    }
  }
}

object ActuatorApiSpec {

  import io.circe.generic.auto._
  import io.circe.parser._


  val contentTypeJson = `Content-Type`(`application/json`)

  def parseBuildInfo(json: String): Either[circe.Error, BuildInfo] =
    decode[BuildInfo](json)

}
