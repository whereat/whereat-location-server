import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import routes.Routes
import db.LocationDao
import cfg.Config

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */


object Main extends App with Config with Routes {

  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  Http().bindAndHandle(route(LocationDao), httpInterface, httpPort)

  println(s"Server online at http://localhost:$httpPort")

}
