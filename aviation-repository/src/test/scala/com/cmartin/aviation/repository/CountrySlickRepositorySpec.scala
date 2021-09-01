package com.cmartin.aviation.repository

import org.scalatest.Inside._

import scala.concurrent.Await

import Common.dao
import TestData._
import Model.CountryDbo

class CountrySlickRepositorySpec extends BaseRepositorySpec {
  import dao.runAction

  behavior of "Country Slick Repository"

  "Insert" should "insert a Country into the repository" in {
    val result = for {
      id <- dao.countryRepository.insert(spainDbo)
      count <- dao.countryRepository.count()
    } yield (id, count)

    result map {
      case (id, count) =>
        assert(id > 0)
        count shouldBe 1
    }
  }

  it should "insert a sequence of countries into the database" in {
    val result = for {
      cs <- dao.countryRepository.insert(countrySequence)
    } yield cs

    result map { cs =>
      assert(cs.nonEmpty, "empty sequence")
      assert(cs.size == countrySequence.size, "invalid sequence size")
      assert(cs.forall(_ > 0L), "non positive entity identifier")
    }
  }

  it should "fail to insert a duplicate Country into the repository" in {
    recoverToSucceededIf[java.sql.SQLException] {
      for {
        _ <- dao.countryRepository.insert(spainDbo)
        _ <- dao.countryRepository.insert(spainDbo)
      } yield ()
    }
  }

  "Update" should "update a country from the database" in {
    val result = for {
      cid <- dao.countryRepository.insert(spainDbo)
      created <- dao.countryRepository.findById(Option(cid))
      _ <- dao.countryRepository.update(created.get.copy(name = updatedSpainText))
      updated <- dao.countryRepository.findById(Option(cid))
    } yield (cid, updated)

    result map {
      case (cid, country) =>
        assert(cid > 0L)
        assert(country.isDefined)
        inside(country.get) {
          case CountryDbo(name, code, _) =>
            name shouldBe updatedSpainText
            code shouldBe spainCode
        }
    }
  }

  "Delete" should "delete a country from the database" in {
    val result = for {
      cid <- dao.countryRepository.insert(spainDbo)
      did <- dao.countryRepository.delete(cid)
      count <- dao.countryRepository.count()
    } yield (cid, did, count)

    result map {
      case (cid, dCount, count) =>
        assert(cid > 0L)
        assert(dCount == 1)
        assert(count == 0)
    }
  }

  it should "delete all countries from the database" in {
    val result = for {
      cs <- dao.countryRepository.insert(countrySequence)
      fs <- dao.countryRepository.findAll()
      ds <- dao.countryRepository.deleteAll()
    } yield (cs, fs, ds)

    result map {
      case (cs, fs, ds) =>
        assert(cs.size == fs.size)
        assert(cs.size == ds)
    }
  }

  "Find" should "retrieve a country by its code" in {
    val result = for {
      _ <- dao.countryRepository.insert(spainDbo)
      country <- dao.countryRepository.findByCode(spainCode)
    } yield country

    result map { country =>
      assert(country.isDefined)
      inside(country.get) {
        case CountryDbo(name, code, _) =>
          name shouldBe spainText
          code shouldBe spainCode
      }
    }
  }

  it should "retrieve all countries from the database" in {
    val result = for {
      _ <- dao.countryRepository.insert(countrySequence)
      cs <- dao.countryRepository.findAll()
    } yield cs

    result map { seq =>
      assert(seq.size == 2)
    }
  }

  override protected def beforeEach(): Unit = {
    Await.result(dao.createSchema(), waitTimeout)
  }

  override protected def afterEach(): Unit = {
    Await.result(dao.dropSchema(), waitTimeout)
  }
}
