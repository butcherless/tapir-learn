package com.cmartin.learn.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteConcatenation._
import com.cmartin.learn.api.ApiModel._
import com.cmartin.learn.domain.ApiConverters
import com.cmartin.learn.domain.DomainModel.Transfer
import sttp.tapir.server.akkahttp._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

trait TransferApi {

  lazy val routes: Route =
    getRoute ~
      postRoute ~
      getComOutputRoute ~
      getShaOutputRoute ~
      getACEntityRoute // ~ add more routes


  // tapir endpoint description to akka routes via .toRoute function
  lazy val getRoute: Route =
    TransferEndpoint.getTransferEndpoint.toRoute(
      businessLogic _ andThen
        handleErrors
    )

  // dummy business process
  lazy val postRoute: Route =
    TransferEndpoint.postTransferEndpoint.toRoute { inDto =>
      val transfer: Transfer = ApiConverters.apiToModel(inDto)
      val outDto: TransferDto = ApiConverters.modelToApi(transfer)
      Future.successful(Right(outDto))
    }


  case class BusinessException(code: Int, message: String = "Information not available") extends RuntimeException(message)

  // simulating business logic function
  def businessLogic(transferId: TransferId): Future[TransferDto] =
    transferId match {
      case StatusCodes.BadRequest.intValue => Future.failed(BusinessException(StatusCodes.BadRequest.intValue, StatusCodes.BadRequest.defaultMessage))
      case StatusCodes.NotFound.intValue => Future.failed(BusinessException(StatusCodes.NotFound.intValue, StatusCodes.NotFound.defaultMessage))
      case StatusCodes.InternalServerError.intValue => Future.failed(BusinessException(StatusCodes.InternalServerError.intValue, StatusCodes.InternalServerError.defaultMessage))
      case StatusCodes.ServiceUnavailable.intValue => Future.failed(BusinessException(StatusCodes.ServiceUnavailable.intValue, StatusCodes.ServiceUnavailable.defaultMessage))
      case 666 => Future.failed(BusinessException(666, "Unknown error"))
      case _ => Future.successful(TransferEndpoint.transferExample)
    }

  def handleErrors[T](f: Future[T]): Future[Either[ErrorInfo, T]] =
    f.transform {
      case Success(v) => Success(Right(v))
      case Failure(BusinessException(code, message)) => code match {
        case StatusCodes.BadRequest.intValue => Success(Left(BadRequestError("MISSING_INFO", message)))
        case StatusCodes.NotFound.intValue => Success(Left(NotFoundError("ENTITY_NOT_FOUND", message)))
        case StatusCodes.InternalServerError.intValue => Success(Left(ServerError("SERVER_ERROR", message)))
        case StatusCodes.ServiceUnavailable.intValue => Success(Left(ServiceUnavailableError("SERVICE_UNAVAILABLE_ERROR", message)))
        case _ => Success(Left(ApiModel.UnknownError("UNKNOWN_ERROR", message)))
      }
      case _ => Success(Left(ApiModel.UnknownError("UNKNOWN_ERROR", "No error description")))
    }


  lazy val getACEntityRoute: Route =
    TransferEndpoint.getACEntityEndpoint.toRoute { _ =>
      Future.successful(Right(TransferEndpoint.acEntityExample))
    }


  lazy val getComOutputRoute: Route =
    TransferEndpoint.getComOutputEndpoint.toRoute { _ =>
      Future.successful(Right(ApiModel.ComOut))
    }

  lazy val getShaOutputRoute: Route =
    TransferEndpoint.getShaOutputEndpoint.toRoute { _ =>
      Future.successful(Right(ApiModel.ShaOut))
    }
}

object TransferApi extends TransferApi