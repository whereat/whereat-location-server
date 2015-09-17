package actors

import akka.actor.{ActorSystem, Props, Cancellable}
import db.LocationDaoImpl
import scala.concurrent.duration._

/**
 * Author: @aguestuser
 * Date: 9/16/15
 * License: GPLv2 (https://www.gnu.org/licenses/gpl-2.0.html)
 */

trait Erasable {
    def eraseHourly(system: ActorSystem, dao: LocationDaoImpl) : Cancellable = {
      val eraseActor = system.actorOf(Props[EraseActor])
      system.scheduler.schedule(0 millis, 1 hour, eraseActor, Erase(dao))
    }
}