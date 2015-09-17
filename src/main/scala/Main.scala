import java.util.concurrent.TimeUnit.{HOURS, MILLISECONDS}

import actors.{Erase, EraseActor}
import akka.actor.{ActorSystem, Cancellable, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import cfg.Config
import db.{LocationDaoImpl, LocationQueries}
import routes.Routes

import scala.concurrent.duration.Duration


/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */


object Main extends App  with Config with Routes with LocationQueries {

  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  db.run(createSchema).map { _ ⇒
    Http().bindAndHandle(route(LocationDaoImpl(db)), httpInterface, httpPort)
    println(s"Server online at http://localhost:$httpPort")
    eraseHourly(system)
  }

  def eraseHourly(system: ActorSystem) : Cancellable = {
    val eraseActor = system.actorOf(Props[EraseActor])
    system.scheduler.schedule(
      Duration(0, MILLISECONDS),
      Duration(1, HOURS),
      eraseActor,
      Erase
    )
  }
}