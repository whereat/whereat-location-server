package routes

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import db.LocationDao
import model.{User, JsonProtocols, Location, WrappedLocation}

import scala.concurrent.ExecutionContextExecutor

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

trait Routes extends CorsSupport with JsonProtocols {

  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  def route[T <: LocationDao](dao: T): Route =
    corsHandler {
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
          path("init") {
            post {
              entity(as[Location]) { loc ⇒
                completeWith(instanceOf[Seq[Location]]) {
                  completer ⇒ dao.init(loc) map completer
                }
              }
            }
          } ~
          path("refresh") {
            post {
              entity(as[WrappedLocation]) { case WrappedLocation(lastPing, loc) ⇒
                completeWith(instanceOf[Seq[Location]]) {
                  completer ⇒ dao.refresh(lastPing, loc) map completer
                }
              }
            }
          } ~
          path("remove") {
            post {
              entity(as[User]) { case User(id) ⇒
                complete {
                  dao.remove(id) map { n ⇒ s"$n record(s) deleted." }
                }
              }
            }
          } ~
          path("erase") {
            post {
              complete {
                dao.erase map { n ⇒ s"Database erased. $n record(s) deleted."}
              }
            }
          }
        }
    }
}