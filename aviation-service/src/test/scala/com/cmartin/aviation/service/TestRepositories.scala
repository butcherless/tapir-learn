package com.cmartin.aviation.service

import zio.ZIO

import java.sql.SQLTimeoutException

//TODO rename or move functions
object TestRepositories {

  def failDefault() =
    ZIO.fail(new SQLTimeoutException("statement timeout reached"))

}
