package actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import db.LocationDao
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.Future

/**
 * Author: @aguestuser
 * License: GPLv2 (https://www.gnu.org/licenses/gpl-2.0.html)
 */

class EraseActorSpec(_system: ActorSystem)
  extends TestKit(_system)
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with MockFactory {

  def this() = this(ActorSystem("EraseActorSpecSystem"))
  override def afterAll() = TestKit.shutdownActorSystem(system)

  "EraseActor" should {

    "erase the database wrapped in an Erase message" in {

      val fakeDao = mock[LocationDao]
      fakeDao.erase _ expects() returning Future.successful(1)

      val eraseActorRef = TestActorRef[EraseActor]
      eraseActorRef ! Erase(fakeDao)
    }
  }
}

