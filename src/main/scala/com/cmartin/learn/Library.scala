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

/*
/**
  * Type class de utilidades para test.
  *
  * @tparam A tipo al que se le aplican las operaciones de la type class.
  */
trait StringExtensions[A] {
  /**
    * Función para eliminar espacios y saltos de línea.
    *
    * @param a tipo al que se le aplica la función
    * @return representación del tipo como String
    */
  def removeSpaces(a: A): String
}

object StringExtensions {

  def removeSpaces[A: StringExtensions](a: A): String =
    StringExtensions[A].removeSpaces(a)

  def apply[A](implicit rs: StringExtensions[A]): StringExtensions[A] = rs

  implicit class StringExtensionsOps[A: StringExtensions](a: A) {
    def removeSpaces: String = StringExtensions[A].removeSpaces(a)
  }

  implicit val stringExtensions: StringExtensions[String] =
    (s: String) => s.replaceAll("[\\n\\s]", "")

}

 */