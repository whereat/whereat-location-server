/*
 * Copyright (c) 2015-present, Total Location Test Paragraph.
 * All rights reserved.
 *
 * This file is part of Where@. Where@ is free software:
 * you can redistribute it and/or modify it under the terms of
 * the GNU General Public License (GPL), either version 3
 * of the License, or (at your option) any later version.
 *
 * Where@ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. For more details,
 * see the full license at <http://www.gnu.org/licenses/gpl-3.0.en.html>
 */

package io.whereat

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import io.whereat.actor.{Erasable, EraseActor}
import io.whereat.config.Config
import io.whereat.db.LocationDaoImpl
import io.whereat.route.Routes

import scala.concurrent.duration._


object Main extends App with Config with Routes with Erasable {

  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  val dao = LocationDaoImpl(db)
  val eraseActor = system.actorOf(Props[EraseActor])

  dao.build map { _ ⇒

    Http().bindAndHandle(route(LocationDaoImpl(db)), httpInterface, httpPort)
    println(s"Server online at http://localhost:$httpPort")

    scheduleErase(system, eraseActor, dao, 1 hour)
  }
}
