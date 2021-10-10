package com.cmartin.aviation.service

import com.cmartin.aviation.repository.AirlineRepository
import com.cmartin.aviation.repository.AirportRepository
import com.cmartin.aviation.repository.CountryRepository
import com.cmartin.aviation.repository.Model._
import com.cmartin.aviation.repository.TestData._
import zio.IO
import zio.Task

import java.sql.SQLTimeoutException

//TODO rename or move functions
object TestRepositories {

  def failDefault() =
    IO.fail(new SQLTimeoutException("statement timeout reached"))

}
