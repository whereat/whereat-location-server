package routes

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import model.{Location, JsonProtocols}

import scala.concurrent.ExecutionContextExecutor

trait Routes extends JsonProtocols {

  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  def echo (loc: Location)(completer: Location ⇒ Unit) = completer(loc)

  val route =
    path("hello") {
      get {
        complete {
          "hello world!" }
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