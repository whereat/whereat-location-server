package db

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest._
import slick.driver.H2Driver.api._
import support.SampleData.{n17, s17}

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

class LocationDaoSpec extends WordSpec
with LocationQueries
with Matchers
with BeforeAndAfter
with ScalaFutures {

  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))
  implicit val ec_ = scala.concurrent.ExecutionContext.global

  var dao: LocationDaoImpl = _

  before {
    dao = LocationDaoImpl(Database.forConfig("testDb2"))
    dao.db.run {
      for {
        _ ← createSchema
        _ ← insert(s17)
      } yield {}
    }.futureValue
  }

  after {
    dao.db.run(dropSchema).futureValue
    dao.db.close()
  }

  "The Location DAO" when {

    "handling an initial ping" should {

      "record the location and return all locations in the DB" in {
        dao.recordInit(n17).futureValue shouldEqual Seq(s17, n17)
      }
    }

    "handling a refresh ping" should {

      "record the location and return all locations since last ping" in {
        dao.db.run(insert(n17)).futureValue
        dao.recordRefresh(n17, s17.time).futureValue shouldEqual Seq(n17)
      }
    }
  }
}
