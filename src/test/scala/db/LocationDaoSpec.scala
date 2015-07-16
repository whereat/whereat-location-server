package db

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest._
import slick.driver.H2Driver.api._
import support.SampleData.{n17, s17}
import scala.concurrent.ExecutionContext.global

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

class LocationDaoSpec extends WordSpec
with LocationQueries
with Matchers
with BeforeAndAfterAll
with BeforeAndAfterEach
with ScalaFutures {

  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))
  implicit val ec = scala.concurrent.ExecutionContext.global

  object fakeLocationDao extends LocationDao {
    val db = Database.forConfig("testDb")
  }

  override def beforeAll() = fakeLocationDao.db.run(createSchema).futureValue

  override def afterAll() = fakeLocationDao.db.close()

  override def beforeEach() = fakeLocationDao.db.run(insert(s17)).futureValue

  override def afterEach() = fakeLocationDao.db.run(delete(s17.id)).futureValue

  "LocationDao" when {

    "handling an initial ping" should {

      "record the location and return all locations in the DB" in {
        fakeLocationDao.recordInit(n17).futureValue shouldEqual Seq(s17, n17)
      }
    }

    "handling a refresh ping" should {

      "record the location and return all locations since last ping" in {
        fakeLocationDao.recordRefresh(n17, s17.time).futureValue shouldEqual Seq(n17)
      }

    }
  }

}
