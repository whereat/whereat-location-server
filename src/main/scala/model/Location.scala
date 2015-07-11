package model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol


/**
 * Author: @aguestuser
 * Date: 7/10/15
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */


case class Location(id: String, lat: Double, lon: Double, time: Long)
object Location

trait LocationJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val locationFormat = jsonFormat4(Location.apply)
}
