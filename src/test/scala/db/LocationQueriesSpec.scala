package db

import org.scalatest.time.{Span, Seconds}
import org.scalatest.{Matchers, WordSpec, BeforeAndAfter, FunSuite}
import org.scalatest.concurrent.ScalaFutures
import slick.driver.H2Driver.api._
import slick.jdbc.meta._
import support.SampleData.{s17, n17}

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
  implicit val ec = scala.concurrent.ExecutionContext.global

  var db: Database = _

  before {
    db = Database.forConfig("testDb")
  }
  after {
    db.close()
  }

  "The Locations Table" should {

    "create a schema" in {

      val tables = db.run {
        for {
          _ ← createSchema
          ts ← MTable.getTables
        } yield ts
      }.futureValue

      tables.size shouldEqual 1
      tables.count(_.name.name == "LOCATIONS") shouldEqual 1
    }

    "insert a location" in {

      val insertCount = db.run {
        for {
          _ ← createSchema
          ic ← insert(s17)
        } yield ic
      }.futureValue

      insertCount shouldEqual 1
    }

    "insert many locations" in {

      val insertCount = db.run {
        for {
          _ ← createSchema
          ic ← insertMany(Seq(s17, n17))
        } yield ic
      }.futureValue

      insertCount shouldEqual Some(2)
    }

    "retrieve all locations" in {

      val locs = db.run {
        for {
          _ ← createSchema
          _ ← insertMany(Seq(s17, n17))
          ls ← locations.result
        } yield ls
      }.futureValue

      locs shouldEqual Seq(s17, n17)
    }

    "retrieve one location" in {

      val loc = db.run {
        for {
          _ ← createSchema
          _ ← insert(s17)
          l ← fetch(s17.id).result.head
        } yield l
      }.futureValue

      loc shouldEqual s17
    }

    "retrieve all locations since a given time" in {

      val locs = db.run {
        for {
          _ ← createSchema
          _ ← insertMany(Seq(s17, n17))
          ls ← allSince(s17.time).result
        } yield ls
      }.futureValue

      db.run(locations.size.result).futureValue shouldEqual 2
      locs shouldEqual Seq(n17)

    }

    "delete all locations" in {

      val rowCount = db.run {
        for {
          _ ← createSchema
          _ ← insertMany(Seq(s17, n17))
          _ ← clear
          n ← locations.size.result
        } yield n
      }.futureValue

      rowCount shouldEqual 0
    }

  }

}
