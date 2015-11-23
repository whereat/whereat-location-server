package actors

import actors.ReadCacheActor.Refresh
import actors.ReadCacheSupervisor.{GetCache, CacheOf}
import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.testkit.{TestProbe, TestActorRef, TestKit}
import akka.util.Timeout
import db.LocationDao
import model.Location
import org.scalatest.concurrent.ScalaFutures
import support.ActorSpec
import support.SampleData.crowd

import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * Written with love for where@
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

class ReadCacheSupervisorSpec(_system: ActorSystem)
  extends ActorSpec(_system)
  with ScalaFutures {

  def this() = this(ActorSystem("ReadCacheSupervisorSpecActorSystem"))
  override def afterAll() = TestKit.shutdownActorSystem(system)

  implicit val timeout = Timeout(5 seconds)
  implicit val ec = system.dispatcher
  implicit val dao = mock[LocationDao]

  val zeroSec = 0L
  val thirtySec = 30000L

  val skedInterval = 200 milli
  val offset = 20 milli

  "ReadCacheSupervisor" should {

    "create a map of caches and handle messages to retrieve them" in {

      implicit val now = () ⇒ zeroSec
      dao.getSince _ expects zeroSec - ReadCache5Sec.interval returning Future.successful(Seq[Location]())
      dao.getSince _ expects zeroSec - ReadCache30Sec.interval returning Future.successful(Seq[Location]())
      dao.getSince _ expects zeroSec - ReadCache1Min.interval returning Future.successful(Seq[Location]())

      val ar = TestActorRef(ReadCacheSupervisor.props(200, List(ReadCache5Sec, ReadCache30Sec, ReadCache1Min)))

      ar shouldBe a [ActorRef]

      ar ? CacheOf(ReadCache5Sec) futureValue match {
        case Some(child) ⇒
          child shouldBe an [ActorRef]
          child.asInstanceOf[ActorRef].path.toString shouldEqual
            "akka://ReadCacheSupervisorSpecActorSystem/user/5SecReadCacheActor"
      }

      ar ? CacheOf(ReadCache30Sec) futureValue match {
        case Some(child) ⇒
          child shouldBe an [ActorRef]
          child.asInstanceOf[ActorRef].path.toString shouldEqual
            "akka://ReadCacheSupervisorSpecActorSystem/user/30SecReadCacheActor"
      }

      ar ? CacheOf(ReadCache1Min) futureValue match {
        case Some(child) ⇒
          child shouldBe an [ActorRef]
          child.asInstanceOf[ActorRef].path.toString shouldEqual
            "akka://ReadCacheSupervisorSpecActorSystem/user/1MinReadCacheActor"
      }
    }

    "handle requests to retrieve non-existent caches" in {

      implicit val now = () ⇒ 0L
      val ar = TestActorRef(ReadCacheSupervisor.props(200, List[ReadCacheWindow]()))

      ar shouldBe an [ActorRef]
      (ar ? CacheOf(ReadCache5Sec) futureValue) shouldEqual None
    }

    "retrieve values from caches" in {

      implicit val now = () ⇒ thirtySec
      dao.getSince _ expects (now() - ReadCache5Sec.interval) returning Future.successful(crowd.drop(5))
      dao.getSince _ expects (now() - ReadCache30Sec.interval) returning Future.successful(crowd)

      val windows = List(ReadCache5Sec, ReadCache30Sec)
      val supervisor = TestActorRef(ReadCacheSupervisor.props(200, windows))

      supervisor ! GetCache(ReadCache5Sec)
      expectMsg(crowd.drop(5))

      supervisor ! GetCache(ReadCache30Sec)
      expectMsg(crowd)
    }

    "retrieve empty Seq from non-existent caches" in {
      implicit val now = () ⇒ thirtySec
      dao.getSince _ expects (now() - ReadCache5Sec.interval) returning Future.successful(crowd.drop(5))
      dao.getSince _ expects (now() - ReadCache30Sec.interval) returning Future.successful(crowd)

      val windows = List(ReadCache5Sec, ReadCache30Sec)
      val supervisor = TestActorRef(ReadCacheSupervisor.props(200, windows))

      supervisor ! GetCache(ReadCache1Min)
      expectMsg(Seq[Location]())
    }

    "schedule cache refreshes and log results" in {

      implicit val now = () ⇒ thirtySec
      val probes = Seq(TestProbe(), TestProbe())
      val caches = Map[ReadCacheWindow,ActorRef](ReadCache5Sec → probes.head.ref, ReadCache30Sec → probes(1).ref)

      class FakeReadCacheSupervisor(cacheTtl: Int, windows: List[ReadCacheWindow]) extends ReadCacheSupervisor (cacheTtl, windows){
        override val children = caches
      }

      val windows = List(ReadCache5Sec, ReadCache30Sec)
      val supervisor = TestActorRef(new FakeReadCacheSupervisor(200, windows))

      probes foreach { p ⇒
        p.expectMsg(skedInterval, Refresh(now))
        p.expectMsgAllOf(2 * skedInterval + offset, Refresh(now), Refresh(now))
        p.expectMsgAllOf(3 * skedInterval + offset, Refresh(now), Refresh(now), Refresh(now))
      }

    }
  }
}
