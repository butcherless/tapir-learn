package com.cmartin.learn

object Library {

  import zio.UIO

  val TEXT = "simple-application-hello"

  def echo(message: String): String = {
    message
  }

  def sum(a: Int, b: Int): UIO[Int] = {
    UIO.effectTotal(a + b)
  }

}
