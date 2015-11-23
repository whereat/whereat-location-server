package support

import akka.actor.ActorSystem
import akka.testkit.{TestKit, ImplicitSender}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{WordSpecLike, Matchers, BeforeAndAfterAll}

/**
 * Written with love for where@
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

class ActorSpec(_system: ActorSystem)
  extends TestKit(_system)
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with MockFactory {

}
