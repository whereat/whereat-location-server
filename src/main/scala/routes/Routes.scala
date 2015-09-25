package routes

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import db.LocationDao
import model._

import scala.concurrent.ExecutionContextExecutor

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

trait Routes extends Headers with JsonProtocols {

  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  def route[T <: LocationDao](dao: T): Route =
    headers {
      path("hello") {
        get {
          complete {
            "hello world!"
          }
        }
      } ~
      pathPrefix("locations") {
        path("update") {
          post {
            entity(as[WrappedLocation]) { wLoc ⇒
              completeWith(instanceOf[Seq[Location]]) {
                completer ⇒ dao.put(wLoc) map completer
              }
            }
          }
        } ~
          path("remove") {
            post {
              entity(as[User]) { case User(id) ⇒
                completeWith(instanceOf[Message]) {
                  completer ⇒ dao.remove(id) map { n ⇒
                    Message(s"$n record(s) deleted.")
                  } map completer
                }
              }
            }
          } ~
          path("erase") {
            post {
              completeWith(instanceOf[Message]) {
                  completer ⇒ dao.erase map { n ⇒
                    Message(s"Database erased. $n record(s) deleted.")
                  } map completer
                }
              }
            }
        }
    }
}