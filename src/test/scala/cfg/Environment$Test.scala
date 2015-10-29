package cfg

import org.scalatest.{Matchers, WordSpecLike}

/**
 * Author: @aguestuser
 * Date: 10/28/15
 * License: GPLv2 (https://www.gnu.org/licenses/gpl-2.0.html)
 */

class Environment$Test extends WordSpecLike with Matchers {

  "`of` factory method" should {

    "parse `Dev` object from 'DEVELOPMENT' string" in {
      Environment.of { "DEVELOPMENT" } shouldEqual Dev
    }

    "parse `Local` object from 'LOCAL' string" in {
      Environment.of { "LOCAL" } shouldEqual Local
    }

    "parse `Prod` object from `PRODUCTION` or other string" in {
      Environment.of { "PRODUCTION" } shouldEqual Prod
      Environment.of { "FOOBAR" } shouldEqual Prod
    }
  }
}
