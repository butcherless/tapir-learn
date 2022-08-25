package com.cmartin.aviation.service

import zio.Runtime.{default => runtime}
import zio.{IO, Unsafe, ZIO}

import java.sql.SQLTimeoutException

//TODO rename or move functions
object TestRepositories {

  def failDefault(): IO[SQLTimeoutException, Nothing] =
    ZIO.fail(new SQLTimeoutException("statement timeout reached"))

  def unsafeRun[E, A](program: ZIO[Any, E, A]): A =
    Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(program)
        .getOrThrowFiberFailure()
    }

}
