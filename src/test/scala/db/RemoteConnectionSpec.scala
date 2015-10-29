package db

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfter, WordSpec, Matchers}
import org.scalatest.time.{Seconds, Span}
import slick.jdbc.meta.MTable
import slick.driver.H2Driver.api._


/**
 * Author: @aguestuser
 * Date: 10/29/15
 * License: GPLv2 (https://www.gnu.org/licenses/gpl-2.0.html)
 */

class RemoteConnectionSpec extends WordSpec
  with LocationQueries
  with Matchers
  with BeforeAndAfter
  with ScalaFutures {

  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))
  var db: Database = _

  before {
    db = Database.forConfig("remoteDb")
    db.run(createSchema).futureValue
  }
  after {
    db.run(dropSchema).futureValue
    db.shutdown.futureValue
  }

  "The Locations SQL Interface" should {

    "create a schema" in {

      val tables = db.run {
        for {
          ts ← MTable.getTables
        } yield ts
      }.futureValue

      tables.size shouldEqual 1
      tables.count(_.name.name == "LOCATIONS") shouldEqual 1
    }
  }
}
