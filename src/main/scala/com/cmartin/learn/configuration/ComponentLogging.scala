package com.cmartin.learn.configuration

import org.slf4j.Logger
import org.slf4j.LoggerFactory

trait ComponentLogging {
  val log: Logger = LoggerFactory.getLogger(getClass)
}
