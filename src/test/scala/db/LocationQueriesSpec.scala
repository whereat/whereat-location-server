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
          _ <- createSchema
          ts <- MTable.getTables
        } yield ts
      }.futureValue

      tables.size shouldEqual 1
      tables.count(_.name.name == "LOCATIONS") shouldEqual 1
    }

    "insert a location" in {

      val insertCount = db.run {
        for {
          _ <- createSchema
          ic <- insert(s17)
        } yield ic
      }

      insertCount.futureValue shouldEqual 1
    }

    "retrieve a location" in {

      val locs = db.run {
        for {
          _ <- createSchema
          _ <- insert(s17)
          ls <- locations.result
        } yield ls
      }

      locs.futureValue shouldEqual Seq(s17)
    }

    "retrieve all locations" in {

      val locs = db.run {
        for {
          _ <- createSchema
          _ <- insert(s17)
          _ <- insert(n17)
          ls <- locations.result
        } yield ls
      }

      locs.futureValue shouldEqual Seq(s17, n17)
    }

    "retrieve all locations since a given time" in {

      val locs = db.run {
        for {
          _ <- createSchema
          _ <- insert(s17)
          _ <- insert(n17)
          ls <- allSince(s17.time).result
        } yield ls
      }.futureValue

      db.run(locations.size.result).futureValue shouldEqual 2
      locs shouldEqual Seq(n17)

    }

  }


}
