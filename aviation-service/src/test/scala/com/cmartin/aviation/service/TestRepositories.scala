package com.cmartin.aviation.service

import zio.IO

import java.sql.SQLTimeoutException

//TODO rename or move functions
object TestRepositories {

  def failDefault() =
    IO.fail(new SQLTimeoutException("statement timeout reached"))

}
