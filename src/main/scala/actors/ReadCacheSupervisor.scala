package actors

import akka.actor.{Actor, ActorRef, Props}
import db.LocationDao

/**
 * Written with love for where@
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

object ReadCacheSupervisor {
  case object Run
  case class Reschedule(child: ActorRef)

  def props[T <: LocationDao](cacheTtl: Int)(implicit now: Function0[Long], dao: T): Props =
    Props(new ReadCacheSupervisor(cacheTtl))
}

class ReadCacheSupervisor[T <: LocationDao](cacheTtl: Int)(implicit now: Function0[Long], dao: T) extends Actor {

  implicit val ec = context.dispatcher
  val sys = context.system
  val children = Map(5 → childOf(5L))

  def receive = {
    case _ ⇒ Unit
  }

  protected def childOf(locTtl: Long): ActorRef =
    sys.actorOf(ReadCacheActor.props(locTtl, cacheTtl), s"${locTtl}sReadCacheActor")
}
