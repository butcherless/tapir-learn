package com.cmartin.learn

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.cmartin.learn.configuration.ApiConfiguration

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/*
  https://doc.akka.io/docs/akka-http/current/server-side/graceful-termination.html
 */
object AkkaWebServerApp
    extends App
    with ApiConfiguration {

  // A K K A  A C T O R  S Y S T E M
  implicit lazy val system: ActorSystem           = ActorSystem("WebActorSystem")
  implicit val executionContext: ExecutionContext = system.dispatcher
  system.log.info(s"Starting WebServer")

  // L A U N C H  W E B  S E R V E R (actor based)
  val bindingFuture: Future[Http.ServerBinding] =
    Http()
      .newServerAt(serverAddress, serverPort)
      .bind(routes)
      .map(_.addToCoordinatedShutdown(hardTerminationDeadline = 10.seconds))

  // Web Server start up management
  bindingFuture.onComplete {
    case Success(binding) =>
      val address = binding.localAddress
      system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)

    case Failure(ex) =>
      system.log.error(s"Failed to bind HTTP endpoint, terminating system: $ex")
      system.terminate()
  }

}
