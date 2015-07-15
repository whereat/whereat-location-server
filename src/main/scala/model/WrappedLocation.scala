package model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

case class WrappedLocation(location: Location, lastPing: Long)

object WrappedLocation

trait WrappedLocationJsonProtocol extends LocationJsonProtocol with DefaultJsonProtocol with SprayJsonSupport {
  implicit val wrappedLocationFormat = jsonFormat2(WrappedLocation.apply)
}