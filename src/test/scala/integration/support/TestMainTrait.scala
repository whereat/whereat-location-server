package integration.support

import actors.{Erasable, EraseActor}
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, Materializer}
import db.LocationDaoImpl
import routes.Routes

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}

trait TestMainTrait extends TestConfig with Routes with Erasable {
  implicit val system: ActorSystem = ActorSystem()

  implicit def executor: ExecutionContextExecutor = system.dispatcher

  implicit val materializer: Materializer = ActorMaterializer()

  val dao = LocationDaoImpl(db)
  val eraseActor = system.actorOf(Props[EraseActor])

  def run: Future[Unit] = {

    dao.build map { _ ⇒

      Http().bindAndHandle(route(LocationDaoImpl(db)), httpInterface, httpPort)
      println(s"Server online at http://localhost:$httpPort")

      scheduleErase(system, eraseActor, dao, 1 hour)
    }
  }
}

