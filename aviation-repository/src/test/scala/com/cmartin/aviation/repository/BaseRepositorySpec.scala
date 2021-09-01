package com.cmartin.aviation.repository

import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

abstract class BaseRepositorySpec extends AsyncFlatSpec with Matchers with BeforeAndAfterEach {}
