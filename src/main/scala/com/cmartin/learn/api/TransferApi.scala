package com.cmartin.learn.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteConcatenation._
import com.cmartin.learn.api.Model._
import com.cmartin.learn.domain.ApiConverters._
import com.cmartin.learn.domain.Model.Transfer
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
import zio.Task

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success

trait TransferApi {

  lazy val routes: Route =
    getRoute ~
      getFilteredRoute ~
      getWithHeaderRoute ~
      postRoute ~
      getComOutputRoute ~
      getShaOutputRoute ~
      getACEntityRoute ~
      postJsonRoute

  // tapir endpoint description to akka routes via .toRoute function
  lazy val getRoute: Route =
    AkkaHttpServerInterpreter
      .toRoute(
        TransferEndpoint.getTransferEndpoint
      )(
        //businessLogic _ andThen
        //  handleErrors
        doControllerLogic
      )

  //TODO refactor controller logic with ZIO
  val runtime = zio.Runtime.default
  type EndpointResponse[T] = Future[Either[ErrorInfo, T]]

  // ZIO based controller logic
  def doControllerLogic(transferId: TransferId): EndpointResponse[TransferDto] = {
    val program = for {
      transfer    <- doBusinessLogic(transferId)
      transferDto <- toDto(transfer)
    } yield transferDto
    val x1 = program.mapError(handle2Error)

    runtime.unsafeRunToFuture(x1.either)
  }

  lazy val getFilteredRoute: Route =
    AkkaHttpServerInterpreter
      .toRoute(
        TransferEndpoint.getFilteredTransferEndpoint
      )(_ => Future.successful(Right(TransferEndpoint.transferListExample)))

  //
  lazy val getWithHeaderRoute: Route =
    AkkaHttpServerInterpreter
      .toRoute(
        TransferEndpoint.getWithHeaderTransferEndpoint
      )(_ => Future.successful(Right(())))

  // dummy business process
  lazy val postRoute: Route =
    AkkaHttpServerInterpreter
      .toRoute(
        TransferEndpoint.postTransferEndpoint
      ) { inDto =>
        val transfer: Transfer = inDto.toModel
        // simulated business process
        val outDto: TransferDto = transfer.toApi
        Future.successful(Right(outDto))
      }

  lazy val postJsonRoute: Route =
    AkkaHttpServerInterpreter
      .toRoute(
        TransferEndpoint.postJsonEndpoint
      )(inDto => Future.successful(Right(inDto)))

  lazy val getACEntityRoute: Route =
    AkkaHttpServerInterpreter
      .toRoute(
        TransferEndpoint.getACEntityEndpoint
      )(_ => Future.successful(Right(TransferEndpoint.acEntityExample)))

  lazy val getComOutputRoute: Route =
    AkkaHttpServerInterpreter
      .toRoute(TransferEndpoint.getComOutputEndpoint)(_ => Future.successful(Right(Model.ComOut)))

  lazy val getShaOutputRoute: Route =
    AkkaHttpServerInterpreter
      .toRoute(
        TransferEndpoint.getShaOutputEndpoint
      )(_ => Future.successful(Right(Model.ShaOut)))

  // simulating business logic function
  def doBusinessLogic(transferId: TransferId): Task[Transfer] =
    transferId match {
      case StatusCodes.BadRequest.intValue =>
        Task.fail(
          BusinessException(StatusCodes.BadRequest.intValue, StatusCodes.BadRequest.defaultMessage)
        )
      case StatusCodes.NotFound.intValue =>
        Task.fail(
          BusinessException(StatusCodes.NotFound.intValue, StatusCodes.NotFound.defaultMessage)
        )
      case StatusCodes.InternalServerError.intValue =>
        Task.fail(
          BusinessException(StatusCodes.InternalServerError.intValue, StatusCodes.InternalServerError.defaultMessage)
        )
      case StatusCodes.ServiceUnavailable.intValue =>
        Task.fail(
          BusinessException(StatusCodes.ServiceUnavailable.intValue, StatusCodes.ServiceUnavailable.defaultMessage)
        )

      case 666 => Task.fail(BusinessException(666, "Unknown error"))

      case _ => Task.succeed(TransferEndpoint.transferModelExample)
    }

  // simulating business logic function
  def businessLogic(transferId: TransferId): Future[TransferDto] =
    transferId match {
      case StatusCodes.BadRequest.intValue =>
        Future.failed(BusinessException(StatusCodes.BadRequest.intValue, StatusCodes.BadRequest.defaultMessage))
      case StatusCodes.NotFound.intValue =>
        Future.failed(BusinessException(StatusCodes.NotFound.intValue, StatusCodes.NotFound.defaultMessage))
      case StatusCodes.InternalServerError.intValue =>
        Future.failed(
          BusinessException(StatusCodes.InternalServerError.intValue, StatusCodes.InternalServerError.defaultMessage)
        )
      case StatusCodes.ServiceUnavailable.intValue =>
        Future.failed(
          BusinessException(StatusCodes.ServiceUnavailable.intValue, StatusCodes.ServiceUnavailable.defaultMessage)
        )
      case 666 => Future.failed(BusinessException(666, "Unknown error"))
      case _   => Future.successful(TransferEndpoint.transferExample)
    }

  def handleErrors[T](f: Future[T]): Future[Either[ErrorInfo, T]] =
    f.transform {
      case Success(v) => Success(Right(v))
      case Failure(BusinessException(code, message)) =>
        code match {
          case StatusCodes.BadRequest.intValue          => Success(Left(BadRequestError("MISSING_INFO", message)))
          case StatusCodes.NotFound.intValue            => Success(Left(NotFoundError("ENTITY_NOT_FOUND", message)))
          case StatusCodes.InternalServerError.intValue => Success(Left(ServerError("SERVER_ERROR", message)))
          case StatusCodes.ServiceUnavailable.intValue =>
            Success(Left(ServiceUnavailableError("SERVICE_UNAVAILABLE_ERROR", message)))
          case _ => Success(Left(Model.UnknownError("UNKNOWN_ERROR", message)))
        }
      case _ => Success(Left(Model.UnknownError("UNKNOWN_ERROR", "No error description")))
    }

  def toDto(transfer: Transfer): Task[TransferDto] =
    Task.succeed(
      TransferDto(
        transfer.sender,
        transfer.receiver,
        transfer.amount,
        transfer.currency.toString,
        transfer.date,
        transfer.desc
      )
    )

  def handle2Error(error: Throwable): ErrorInfo = {
    error match {
      case BusinessException(code, message) =>
        code match {
          case StatusCodes.BadRequest.intValue =>
            BadRequestError("MISSING_INFO", message)

          case StatusCodes.NotFound.intValue =>
            NotFoundError("ENTITY_NOT_FOUND", message)

          case StatusCodes.InternalServerError.intValue =>
            ServerError("SERVER_ERROR", message)

          case StatusCodes.ServiceUnavailable.intValue =>
            ServiceUnavailableError("SERVICE_UNAVAILABLE_ERROR", message)

          case _ => Model.UnknownError("UNKNOWN_ERROR", message)
        }
      case e @ _ => UnknownError("UNKNOWN_ERROR", e.getMessage())
    }
  }

  case class BusinessException(code: Int, message: String = "Information not available")
      extends RuntimeException(message)
}

object TransferApi extends TransferApi
