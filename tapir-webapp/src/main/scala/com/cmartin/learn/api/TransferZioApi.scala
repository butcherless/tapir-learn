package com.cmartin.learn.api

import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.ztapir._
import zio.{IO, Task}

import Model.{ErrorInfo, TransferDto}

trait TransferZioApi {
  import TransferZioApi._

  /* - Operation Endpoint
     - Service operation
     - Map errors
     - Server Interpreter
   */

  /* f:Task[A] => IO[ErrorInfo,A]
     implicit function
   */

  val zioGetRoute =
    ZioHttpInterpreter().toHttp(
      TransferEndpoint.getTransferEndpoint.zServerLogic { id =>
        for {
          transfer <- TransferApi.doBusinessLogic(id)
          dto <- TransferApi.toDto(transfer)
        } yield dto
      }
    )
}

object TransferZioApi
    extends TransferZioApi {

  implicit def handle[A](program: Task[A]): IO[ErrorInfo, A] =
    program.mapError(TransferApi.handle2Error)
}
