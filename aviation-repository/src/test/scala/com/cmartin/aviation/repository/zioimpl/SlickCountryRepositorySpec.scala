package com.cmartin.aviation.repository.zioimpl
import com.cmartin.aviation.repository.Model.CountryDbo
import com.cmartin.aviation.repository.zioimpl.SlickCountryRepository.SchemaHelper
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.{FiberFailure, Has, IO, TaskLayer, ZIO, ZLayer}
import slick.jdbc.JdbcProfile
import slick.interop.zio.DatabaseProvider
import com.typesafe.config.ConfigFactory

import java.sql.SQLIntegrityConstraintViolationException
import scala.jdk.CollectionConverters._

class SlickCountryRepositorySpec
    extends AnyFlatSpec with Matchers with BeforeAndAfterEach {

  //TODO
  val runtime = zio.Runtime.default

  private val config = ConfigFactory.parseMap(
    Map(
      "url" -> "jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1",
      "driver" -> "org.h2.Driver",
      "connectionPool" -> "disabled"
    ).asJava
  )

  val configLayer = ZLayer.succeed(config)

  private val env: ZLayer[Any, Throwable, Has[CountryRepository]] =
    (ZLayer.succeed(config) ++ ZLayer.succeed[JdbcProfile](
      slick.jdbc.H2Profile
    )) >>> DatabaseProvider.live >>> SlickCountryRepository.live

  behavior of "SlickCountryRepository"

  "Insert" should "insert a Country into the database" in {
    val dbo = CountryDbo("Spain", "es")
    val program = for {
      repo <- ZIO.service[CountryRepository]
      c <- repo.insert(dbo)
    } yield c

    val layeredProgram = program.provideLayer(env)
    val id = runtime.unsafeRun(layeredProgram)

    assert(id > 0)
  }

  it should "fail to insert a duplicate Country into the database" in {
    val dbo = CountryDbo("Spain", "es")
    val program = for {
      repo <- ZIO.service[CountryRepository]
      c <- repo.insert(dbo)
      c <- repo.insert(dbo)
    } yield c

    val layeredProgram = program.provideLayer(env).either

    val resultEither = runtime.unsafeRun(layeredProgram)

    resultEither.isLeft shouldBe true

    resultEither.swap.map {
      case _: SQLIntegrityConstraintViolationException => succeed
      case _                                           => fail("unexpected error")
    }

  }

  override def beforeEach(): Unit = {
    val init =
      (ZLayer.succeed(config) ++ ZLayer.succeed[JdbcProfile](
        slick.jdbc.H2Profile
      )) >>> DatabaseProvider.live >>> SlickCountryRepository.init

    val program = (for {
      helper <- ZIO.service[SchemaHelper]
      _ <- helper.dropSchema()
      _ <- helper.createSchema()
    } yield ()).provideLayer(init)

    runtime.unsafeRun(program)
  }
}
