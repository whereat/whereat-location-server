package model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

/**
 * License: GPLv2 (https://www.gnu.org/licenses/gpl-2.0.html)
 */

case class Message(msg: String)
object Message

trait MessageJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val messageFormat = jsonFormat1(Message.apply)
}

