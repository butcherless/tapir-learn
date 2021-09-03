package com.cmartin.aviation.domain

import com.cmartin.aviation.port.CountryService
import zio.logging._

import Model._

class CountryCrudService extends CountryService {

  import CountryCrudService._

  override def create(country: Country): ServiceResponse[Country] = {
    val program = for {
      _ <- log.debug(s"create: $country")
    } yield country

    program
  }

  override def findByCode(code: CountryCode): ServiceResponse[Country] = {
    val program = for {
      _ <- log.debug(s"findByCode: $code")
    } yield Country(CountryCode("es"), "Spain")

    program
  }

  override def update(country: Country): ServiceResponse[Country] = {
    val program = for {
      _ <- log.debug(s"update: $country")
    } yield Country(CountryCode("es"), "Spain")

    program
  }

  override def deleteByCode(code: CountryCode): ServiceResponse[Int] = {
    val program = for {
      _ <- log.debug(s"deleteByCode: $code")
    } yield 1

    program
  }

}

object CountryCrudService {
  def apply(): CountryCrudService =
    new CountryCrudService()

}
