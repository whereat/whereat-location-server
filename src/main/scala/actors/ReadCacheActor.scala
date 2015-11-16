package actors

import akka.actor.{Actor, Cancellable, Props}
import db.LocationDao
import model.Location

import scala.concurrent.duration._

/**
 * Written with love for where@
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

object ReadCacheActor {
  
  case object Clear
  case object Get

  case class Time(now: Function0[Long])

  def props[T <: LocationDao](locTtl: Long, cacheTtl: Int)(implicit now: Function0[Long], dao: T) : Props =
    Props(new ReadCacheActor(locTtl, cacheTtl))
}

class ReadCacheActor[T <: LocationDao](locTtl: Long, cacheTtl: Int)(implicit now: Function0[Long], dao: T) extends Actor {

  import ReadCacheActor._
  implicit val ec = context.dispatcher

  var locs = Seq[Location]()

  def receive = {

    case Get ⇒
      sender() ! locs

    case Clear ⇒
      dao.getSince(now() - locTtl) map ( ls ⇒ locs = ls )
  }

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = { super.preStart(); scheduleClear }

  protected def scheduleClear : Cancellable =
    context.system.scheduler.schedule(0 millis, cacheTtl seconds, self, Clear)

}
