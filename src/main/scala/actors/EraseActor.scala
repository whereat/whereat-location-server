package actors

import akka.actor.Actor
import db.LocationDao

/**
 * Author: @aguestuser
 * License: GPLv2 (https://www.gnu.org/licenses/gpl-2.0.html)
 */

case class Erase[T <: LocationDao](dao: T)

class EraseActor extends Actor {
  def receive = {
    case Erase(dao) ⇒ dao.erase
  }
}



