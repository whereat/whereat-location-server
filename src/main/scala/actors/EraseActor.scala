package actors

import akka.actor.Actor
import cfg.Config
import db.LocationQueries

/**
 * Author: @aguestuser
 * Date: 9/16/15
 * License: GPLv2 (https://www.gnu.org/licenses/gpl-2.0.html)
 */

case object Erase

class EraseActor extends Actor with Config with LocationQueries {
  def receive = {
    case Erase ⇒ db.run(clear)
  }
}

