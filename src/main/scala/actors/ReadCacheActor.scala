package actors

import actors.ReadCacheSupervisor.{RefreshFail, RefreshSuccess}
import akka.actor.{Actor, Props}
import db.LocationDao
import model.Location

import scala.util.{Failure, Success}

/**
 * Written with love for where@
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */


object ReadCacheActor {
  
  case class Refresh(now: () ⇒ Long)
  case object Get

  def props[T <: LocationDao](window: ReadCacheWindow)(implicit dao: T) : Props =
    Props(new ReadCacheActor(window))
}

class ReadCacheActor[T <: LocationDao](window: ReadCacheWindow)(implicit dao: T) extends Actor {

  import ReadCacheActor._
  implicit val ec = context.dispatcher

  var locs = Seq[Location]()

  def receive = {

    case Get ⇒
      sender() ! locs

    case Refresh(now) ⇒
      dao.getSince { now() - window.interval } onComplete  {
        case Success(ls) ⇒
          locs = ls
          context.parent ! RefreshSuccess(window, ls.size)
        case Failure(err) ⇒
          context.parent ! RefreshFail(window, err.getMessage)
      }
  }
}
