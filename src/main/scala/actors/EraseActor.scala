package actors

import actors.EraseActor.Erase
import akka.actor.Actor
import db.LocationDao

/**
 * Author: @aguestuser
 * License: GPLv2 (https://www.gnu.org/licenses/gpl-2.0.html)
 */

object EraseActor {
  case class Erase[T <: LocationDao](dao: T)
}

class EraseActor extends Actor {

  implicit val ec = context.dispatcher

  def receive = {

    case Erase(dao) ⇒
      dao.erase map { n ⇒
        println(s"Database erased. $n records deleted.")
        sender() ! n
      }
  }
}



