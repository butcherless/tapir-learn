package com.cmartin.learn.api

import org.apache.pekko.http.scaladsl.model.ContentTypes.`application/json`
import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.testkit.ScalatestRouteTest
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
    Get(s"$BASE_API/health") ~>
      ActuatorApi.route ~>
      // T H E N
      check {
        status shouldBe StatusCodes.OK
        contentType shouldBe `application/json`
        val json = entityAs[String]
        json.contains("gitCommit") shouldBe true
        json.contains("name") shouldBe true
        json.contains("scalaVersion") shouldBe true
        json.contains("version") shouldBe true

      }
  }

}

object ActuatorApiSpec {}
