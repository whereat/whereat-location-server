package cfg

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{Matchers, WordSpecLike}
import slick.jdbc.meta.MTable

/**
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

class EnvironmentSpec extends WordSpecLike
with Matchers
with ScalaFutures {

  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  "`#of`" should {

    "recognize Prod environment" in {
      Environment.of { "PRODUCTION" } shouldEqual Prod
    }

    "recognize Dev environment" in {
      Environment.of { "DEVELOPMENT" } shouldEqual Dev
    }

    "recoginze Test environment" in {
      Environment.of { "TEST" } shouldEqual Test
    }

    "recognize NullEnv environment" in {
      Environment.of { "FOOBAR" } shouldEqual NullEnv
    }
  }

  "`#dbFor`" should {

    "initialize a remote production database" in {
      val db = Environment.dbFor(Prod)
      db.run(MTable.getTables).futureValue should not be empty

      db.shutdown.futureValue
    }

    "initialize a remote development database" in {
      val db = Environment.dbFor(Dev)
      db.run(MTable.getTables).futureValue should not be empty

      db.shutdown.futureValue
    }

    "initialize a remote test database" in {
      val db = Environment.dbFor(Test)
      db.run(MTable.getTables).futureValue should not be empty

      db.shutdown.futureValue
    }
  }
}
