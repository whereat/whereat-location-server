package db

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}
import slick.driver.H2Driver.api._
import slick.jdbc.meta._
import support.SampleData.{n17, s17, s17_}

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

class LocationQueriesSpec
  extends WordSpec
  with LocationQueries
  with Matchers
  with BeforeAndAfter
  with ScalaFutures {

  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))
  var db: Database = _

  before {
    db = Database.forConfig("db.test")
    db.run(createSchema).futureValue
  }
  after {
    db.run(dropSchema).futureValue
    db.shutdown.futureValue
  }

  "The Locations SQL Interface" should {

    "create a schema" in {

      val tables = db.run { MTable.getTables }.futureValue

      tables should not be empty
      tables.count(_.name.name == "LOCATIONS") shouldEqual 1
    }

    "drop a schema" in {

      val tables = db.run {
        for {
          _ ← dropSchema
          ts ← MTable.getTables
        } yield ts
      }.futureValue

      tables.count(_.name.name == "LOCATIONS") shouldEqual 0

      db.run(createSchema).futureValue // <- to accomodate teardown
    }

    "insert a location" in {

      val insertCount = db.run {
        for {
          ic ← insert(s17)
        } yield ic
      }.futureValue

      insertCount shouldEqual 1
    }

    "insert many locations" in {

      val insertCount = db.run {
        for {
          ic ← insertMany(Seq(s17, n17))
        } yield ic
      }.futureValue

      insertCount shouldEqual Some(2)
    }

    "retrieve all locations" in {

      val locs = db.run {
        for {
          _ ← insertMany(Seq(s17, n17))
          ls ← locations.result
        } yield ls
      }.futureValue

      locs shouldEqual Seq(s17, n17)
    }

    "retrieve one location" in {

      val loc = db.run {
        for {
          _ ← insert(s17)
          l ← fetch(s17.id).result.head
        } yield l
      }.futureValue

      loc shouldEqual s17
    }

    "retrieve all locations since a given time" in {

      val locs = db.run {
        for {
          _ ← insertMany(Seq(s17, n17))
          ls ← allSince(s17.time).result
        } yield ls
      }.futureValue

      db.run(locations.size.result).futureValue shouldEqual 2
      locs shouldEqual Seq(n17)

    }

    "update a location" in {

      val updateCount = db.run {
        for {
          _ ← insert(s17)
          n ← update(s17.id, s17_)
        } yield n
      }.futureValue

      updateCount shouldEqual 1
      db.run(fetch(s17.id).result.head).futureValue shouldEqual s17_

    }

    "delete a location" in {

      val rowCount = db.run {
        for {
          _ ← insertMany(Seq(s17, n17))
          _ ← delete(s17.id)
          n ← locations.size.result
        } yield n
      }.futureValue

      rowCount shouldEqual 1
    }

    "delete all locations" in {

      val rowCount = db.run {
        for {
          _ ← insertMany(Seq(s17, n17))
          _ ← clear
          n ← locations.size.result
        } yield n
      }.futureValue

      rowCount shouldEqual 0
    }

    "save a location that isn't in the DB yet" in {

      val opCount = db.run {
        for {
          n ← save(s17)
        } yield n
      }.futureValue

      opCount shouldEqual 1
      db.run(locations.size.result).futureValue shouldEqual 1
      db.run(fetch(s17.id).result.head).futureValue shouldEqual s17
    }

    "save a location that's already in the DB" in {

      val opCounts = db.run {
        for {
          m ← insert(s17)
          n ← save(s17_)
        } yield (m, n)
      }.futureValue

      opCounts shouldEqual (1,1)
      db.run(locations.size.result).futureValue shouldEqual 1
      db.run(fetch(s17.id).result.head).futureValue shouldEqual s17_
    }
  }

}
