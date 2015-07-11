package model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol


/**
 * Author: @aguestuser
 * Date: 7/10/15
 * License: GPLv2 (https://www.gnu.org/licenses/gpl-2.0.html)
 */


case class Location(id: String, lat: Double, lon: Double, time: Long)

object LocationJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val locationFormat = jsonFormat4(Location)
}
