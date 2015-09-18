package actors

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestActorRef}
import com.typesafe.config.ConfigFactory
import db.LocationDao
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.Future

/**
 * Author: @aguestuser
 * License: GPLv2 (https://www.gnu.org/licenses/gpl-2.0.html)
 */

class EraseActorSpec
  extends WordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with MockFactory {

  implicit var system: ActorSystem = _

  override def beforeAll(){ system = ActorSystem("TestActorSystem", ConfigFactory.load()) }
  override def afterAll() = { TestKit.shutdownActorSystem(system) }


  "EraseActor" should {

    "erase the database wrapped in an Erase message" in {

      val fakeDao = mock[LocationDao]
      fakeDao.erase _ expects() returning Future.successful(1)

      val eraseActorRef = TestActorRef[EraseActor]
      eraseActorRef ! Erase(fakeDao)
    }
  }
}

