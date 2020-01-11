package com.cmartin.learn

import com.cmartin.learn.Library._
import zio.test.Assertion._
import zio.test._

object ZioSpec
  extends DefaultRunnableSpec(
    suite("Check test")(
      test("Echo function return the same text") {
        val result = echo(TEXT)
        assert(result, equalTo(TEXT))
      },
      testM("Zio effect sum 2 + 3") {
        for {
          r <- sum(2, 3)
        } yield assert(r, equalTo(5))
      }
    )
  )

