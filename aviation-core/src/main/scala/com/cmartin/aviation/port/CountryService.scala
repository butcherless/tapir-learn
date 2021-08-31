package com.cmartin.aviation.port

import com.cmartin.aviation.domain.Model._
import zio.ZIO
import zio.logging._

trait CountryService {

  type ServiceResponse[A] = ZIO[Logging, ServiceError, A]

  def create(country: Country): ServiceResponse[Country]
  def findByCode(code: CountryCode): ServiceResponse[Country]
  def update(country: Country): ServiceResponse[Country]
  def deleteByCode(code: CountryCode): ServiceResponse[Unit]
}
