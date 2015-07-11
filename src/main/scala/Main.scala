import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import routes.Routes
import cfg.Config

/**
 * Author: @aguestuser
 * Date: 7/10/15
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */


object Main extends App with Config with Routes {

//  import system.dispatcher
//  implicit val system = ActorSystem("whereat-server")
//  implicit val materializer = ActorMaterializer()

  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  Http().bindAndHandle(route, httpInterface, httpPort)

  println(s"Server online at http://localhost:$httpPort")

}
