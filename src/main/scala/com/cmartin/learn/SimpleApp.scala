package com.cmartin.learn

import com.cmartin.learn.Library._
import org.slf4j.LoggerFactory
import zio.{App, Task, ZIO}

// uncomment lines below to run
object SimpleApp
/*
  extends App {

  private val log = LoggerFactory.getLogger(classOf[App])

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    val program = for {
      _ <- Task.effect(log.debug(echo(TEXT)))
      result <- sum(2, 3)
      _ <- Task.effect(log.debug(s"sum result: $result"))
    } yield ()

    program.fold(e => {
      println(e)
      1
    },
      _ => 0)
  }
}


 */