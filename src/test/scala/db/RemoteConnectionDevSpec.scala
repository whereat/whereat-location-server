package db

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{Matchers, WordSpecLike}
import slick.driver.PostgresDriver.api.Database
import slick.jdbc.meta.MTable

/**
 * Written with love for where@
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

class RemoteConnectionDevSpec extends WordSpecLike
with Matchers
with ScalaFutures {

  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  "#Database.for" should {

    "connect to dev DB" in {
      val db = Database.forConfig("db.heroku")
      db.run(MTable.getTables).futureValue should not be empty
    }
  }
}