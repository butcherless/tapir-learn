package com.cmartin.learn

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.cmartin.learn.configuration.ApiConfiguration

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object AkkaWebServerApp
  extends App
    with ApiConfiguration {

  // A K K A  A C T O R  S Y S T E M
  implicit lazy val system: ActorSystem = ActorSystem("WebActorSystem")
  implicit val executionContext: ExecutionContext = system.dispatcher
  system.log.info(s"Starting WebServer")

  // L A U N C H  W E B  S E R V E R (actor based)
  val futureBinding: Future[Http.ServerBinding] =
    Http()
      .bindAndHandle(
        routes, // Rest API routes
        serverAddress, //TODO configuration properties
        serverPort
      )

  // Web Server start up management
  futureBinding.onComplete {
    case Success(binding) =>
      val address = binding.localAddress
      system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)

    case Failure(ex) =>
      system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
      system.terminate()
  }

}
