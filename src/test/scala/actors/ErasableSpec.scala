package actors

import actors.EraseActor.Erase
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import akka.util.Timeout
import db.LocationDao
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

/**
 * Author: @aguestuser
 * License: GPLv2 (https://www.gnu.org/licenses/gpl-2.0.html)
 */

class ErasableSpec(_system: ActorSystem)
  extends TestKit(_system)
  with Erasable
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with MockFactory {

  def this() = this(ActorSystem("EraseActorSpecSystem"))
  override def afterAll() = TestKit.shutdownActorSystem(system)

  implicit val timeout = Timeout(5 seconds)
  val fakeDao = mock[LocationDao]

  "Erasable" when {

    "#scheduleErase called" should {

      "schedule periodic erasures of the DB" in {

        val interval = 200 milli
        val offset = 10 milli
        val eraseActorRef = TestActorRef(new EraseActor)

        scheduleErase(system, testActor, fakeDao, interval)

        expectMsg(interval, Erase(fakeDao))
        expectMsgAllOf(2 * interval + offset, Erase(fakeDao), Erase(fakeDao))
        expectMsgAllOf(3 * interval + offset, Erase(fakeDao), Erase(fakeDao), Erase(fakeDao))
      }
    }
  }
}
