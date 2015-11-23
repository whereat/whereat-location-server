package actors

import actors.ReadCacheActor.{Get, Refresh}
import akka.actor.{Actor, ActorRef, Props}
import akka.util.Timeout
import db.LocationDao
import akka.pattern.ask
import akka.pattern.pipe
import model.Location

import scala.concurrent.duration._

/**
 * Written with love for where@
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */


object ReadCacheSupervisor {

  case class CacheOf(window: ReadCacheWindow)
  case class GetCache(window: ReadCacheWindow)
  case class RefreshSuccess(window: ReadCacheWindow, size: Int)
  case class RefreshFail(window: ReadCacheWindow, msg: String)

  def props[T <: LocationDao](cacheTtl: Int, windows: List[ReadCacheWindow])(implicit now: Function0[Long], dao: T): Props =
    Props(new ReadCacheSupervisor(cacheTtl, windows))
}

class ReadCacheSupervisor[T <: LocationDao](cacheTtl: Int, windows: List[ReadCacheWindow])(implicit now: Function0[Long], dao: T) extends Actor {

  import ReadCacheSupervisor._
  implicit val ec = context.dispatcher
  implicit val timeout = Timeout(5 seconds)
  val children = (Map[ReadCacheWindow, ActorRef]() /: windows)((acc,w) ⇒ acc + (w → childOf(w)))

  def receive = {

    case CacheOf(window) ⇒
      sender() ! children.get(window)

    case GetCache(window) ⇒
      children.get(window) match {
        case Some(cache) ⇒
          cache ? Get pipeTo sender()
        case None ⇒
          sender() ! Seq[Location]()
      }

    case RefreshSuccess(window, size) ⇒
      println(s"${window.name} cleared. Current size: $size")

    case RefreshFail(window, msg) ⇒
      println(s"${window.name} failed to clear. Error: $msg")
  }

  protected def childOf(window: ReadCacheWindow): ActorRef =
    context.system.actorOf(ReadCacheActor.props(window), s"${window.name}Actor")

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    scheduleRefresh
  }

  protected def scheduleRefresh: Unit =
    children.values foreach { child ⇒
      context.system.scheduler.schedule(0 millis, cacheTtl millis, child, Refresh(now))
    }
}
