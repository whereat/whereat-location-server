package db

import org.scalatest.time.{Span, Seconds}
import org.scalatest.{BeforeAndAfter, FunSuite}
import org.scalatest.concurrent.ScalaFutures
import slick.lifted.TableQuery
import slick.driver.H2Driver.api._
import slick.jdbc.meta._
import support.SampleData.s17

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

class LocationsTest extends FunSuite with BeforeAndAfter with ScalaFutures {

  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  val locations = TableQuery[Locations]
  var db: Database = _

  def createSchema() = db.run(locations.schema.create).futureValue
  def insertLocation() = db.run(locations += s17).futureValue

  before { db = Database.forConfig("h2mem1") }

  test("Creating Schema works") {

    createSchema()
    val tables = db.run(MTable.getTables).futureValue

    assert(tables.size == 1)
    assert(tables.count(_.name.name.equalsIgnoreCase("locations")) == 1)
  }


  test("Inserting a location works") {

    createSchema()
    val insertCount = insertLocation()

    assert(insertCount == 1)
  }

  test("Retrieving a location works"){

    createSchema()
    insertLocation()
    val locs = db.run(locations.result).futureValue

    assert(locs.size == 1)
    assert(locs.head == s17)
  }

  after { db.close() }

}
