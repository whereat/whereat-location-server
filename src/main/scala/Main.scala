import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

/**
 * Author: @aguestuser
 * Date: 7/10/15
 * License: GPLv2 (https://www.gnu.org/licenses/gpl-2.0.html)
 */

object Main extends App with Config {

  import system.dispatcher
  implicit val system = ActorSystem("whereat-server")
  implicit val materializer = ActorMaterializer()

  val route =
    path("hello") {
      get {
        complete {
          "hello world!"
        }
      }
    }

  Http().bindAndHandle(route, httpInterface, httpPort)

  println(s"Server online at http://localhost:$httpPort")

}
