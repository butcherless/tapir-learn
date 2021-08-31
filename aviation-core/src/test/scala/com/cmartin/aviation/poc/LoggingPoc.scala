package com.cmartin.aviation.poc

import com.cmartin.aviation.domain.Model._
import zio.UIO
import zio.ZIO
import zio.logging._
import zio.logging.slf4j.Slf4jLogger

object LoggingPoc {

  object Api {
    // TODO service call
  }

  object Service {

    type ServiceResponse[A] = ZIO[Logging, Nothing, A]

    def create(country: Country): ServiceResponse[Country] = {
      for {
        _ <- log.debug(s"create: $country")
        created <- UIO.succeed(Country(CountryCode("es"), "Spain")) // repository insert call
      } yield created
    }
  }

  object Main {}
}
