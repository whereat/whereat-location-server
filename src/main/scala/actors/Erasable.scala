package actors

import akka.actor._
import db.LocationDao

import scala.concurrent.duration._

/**
 * Author: @aguestuser
 * Date: 9/16/15
 * License: GPLv2 (https://www.gnu.org/licenses/gpl-2.0.html)
 */

trait Erasable {
    def scheduleErase[D <: LocationDao](sys: ActorSystem, actor: ActorRef, dao: D, dur: FiniteDuration) : Cancellable = {
      import sys.dispatcher
      sys.scheduler.schedule(0 millis, dur, actor, Erase(dao))
    }
}