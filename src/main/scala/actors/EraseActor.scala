package actors

import akka.actor.Actor
import cfg.Config
import db.{LocationDaoImpl, LocationQueries}

/**
 * Author: @aguestuser
 * License: GPLv2 (https://www.gnu.org/licenses/gpl-2.0.html)
 */

//TODO figure out how to test actors!

case class Erase(dao: LocationDaoImpl)

class EraseActor extends Actor with Config with LocationQueries {
  def receive = {
    case Erase(dao) ⇒ dao.erase
  }
}

