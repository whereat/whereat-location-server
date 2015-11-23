package actors

import actors.ReadCacheActor.{Get, Refresh}
import actors.ReadCacheSupervisor.RefreshSuccess
import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestActorRef, TestKit}
import akka.util.Timeout
import db.LocationDao
import model.Location
import support.ActorSpec
import support.SampleData.march

import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * Written with love for where@
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

class ReadCacheActorSpec(_system: ActorSystem) extends ActorSpec(_system) {

  def this() = this(ActorSystem("EraseActorSpecSystem"))
  override def afterAll() = TestKit.shutdownActorSystem(system)
  implicit val timeout = Timeout(5 seconds)


  implicit val fakeDao = mock[LocationDao]

  "ReachCacheActor" when {

    "building from `props` factory method" should {

      "should produce an Actor wrapping an empty cache" in {
        val actorRef = TestActorRef(ReadCacheActor.props(ReadCache5Sec), "testActor")
        actorRef shouldBe a [ActorRef]

        actorRef ! Get
        expectMsg(Seq[Location]())
      }
    }

    "caching 5 second window" should {

      "refresh cache with all locations written to DB in last 5 seconds" in {

        val actorRef = TestActorRef(ReadCacheActor.props(ReadCache5Sec))

        fakeDao.getSince _ expects 0L returning Future.successful(march.head)
        actorRef ! Refresh(() ⇒ 5000L)
        expectMsg(RefreshSuccess(ReadCache5Sec, 3))
        actorRef ! Get
        expectMsg(march.head)

        fakeDao.getSince _ expects 5000L returning Future.successful(march(1))
        actorRef ! Refresh(() ⇒ 10000L)
        expectMsg(RefreshSuccess(ReadCache5Sec, 3))
        actorRef ! Get
        expectMsg(march(1))

        fakeDao.getSince _ expects 10000L returning Future.successful(march(2))
        actorRef ! Refresh(() ⇒ 15000L)
        expectMsg(RefreshSuccess(ReadCache5Sec, 3))
        actorRef ! Get
        expectMsg(march(2))
      }
    }
  }
}
