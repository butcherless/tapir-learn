package com.cmartin.learn.configuration

import org.slf4j.{Logger, LoggerFactory}

trait ComponentLogging {
  val log: Logger = LoggerFactory.getLogger(getClass)
}
