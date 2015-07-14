package db

import org.scalatest.time.{Span, Seconds}
import org.scalatest.{Matchers, WordSpec, BeforeAndAfter, FunSuite}
import org.scalatest.concurrent.ScalaFutures
import slick.lifted.TableQuery
import slick.driver.H2Driver.api._
import slick.jdbc.meta._
import support.SampleData.s17

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

class LocationsSpec extends WordSpec
  with Matchers
  with BeforeAndAfter
  with ScalaFutures
  with LocationQueries {

  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  //val locations = TableQuery[Locations]
  var db: Database = _

  //def createSchema() = db.run(locations.schema.create).futureValue
  //def insertLocation() = db.run(locations += s17).futureValue

  before { db = Database.forConfig("testDb") }
  after { db.close() }

  "The Locations Table" should {

    "create a schema" in {
//      createSchema()
      db.run(createSchema).futureValue

      val tables = db.run(MTable.getTables).futureValue

      tables.size shouldEqual 1
      tables.count(_.name.name.equalsIgnoreCase("locations")) shouldEqual 1
    }

    "insert a location" in {
//      createSchema()
//      val insertCount = insertLocation()

      db.run(createSchema).futureValue
      val insertCount = db.run(insert(s17)).futureValue

      insertCount shouldEqual 1
    }

    "retrieve a location" in {

      db.run(createSchema).futureValue
      db.run(insert(s17)).futureValue
      val locs = db.run(getAll).futureValue

      locs.size shouldEqual 1
      locs.head shouldEqual s17
    }

  }



}
