package routes

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import db.{LocationDao}
import model.{Location, JsonProtocols}
import scala.concurrent.{ExecutionContextExecutor}

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

trait Routes extends JsonProtocols {

  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  def route[T <: LocationDao](dao: T): Route =
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
            completeWith(instanceOf[Seq[Location]]) {
              completer ⇒ dao.writeAndReport(loc) map completer
            }
          }
        }
      }

}