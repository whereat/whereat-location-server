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

    def scheduleErase(sys: ActorSystem, dao: LocationDaoImpl, dur: FiniteDuration) : Cancellable = {
      implicit val executor = sys.dispatcher
      val eraseActor = sys.actorOf(Props[EraseActor])
      sys.scheduler.schedule(0 millis, dur, eraseActor, Erase(dao))
    }

}