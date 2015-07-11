package routes

import akka.http.scaladsl.server.Directives._
import model.Location
import model.LocationJsonProtocol._

/**
 * Author: @aguestuser
 * Date: 7/10/15
 * License: GPLv2 (https://www.gnu.org/licenses/gpl-2.0.html)
 */

trait Routes {

  def echo (loc: Location)(completer: Location ⇒ Unit) = completer(loc)

  val route =
    path("hello") {
      get {
        complete {
          "hello world!"
        }
      }
    } ~
    path("locations") {
      post {
        entity(as[Location]) { loc ⇒
          completeWith(instanceOf[Location]) {
            completer ⇒ echo(loc)(completer)
          }
        }
      }
    }
}
