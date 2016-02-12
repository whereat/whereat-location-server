/**
 *
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
 *
 */

package io.whereat.route

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import io.whereat.flow.RelayFlows._
import io.whereat.db.LocationDao
import io.whereat.model._

import scala.concurrent.ExecutionContextExecutor


trait Routes extends CorsSupport with PublicKeyPinningSupport with JsonProtocols {

  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  def route[T <: LocationDao](dao: T): Route =
    pkpHandler {
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
          } ~
          path("websocket") {
            get {
              handleWebsocketMessages(
                deserializationFlow
                  .via(errorHandlingFlow.join(Flow[Location]))
                  .via(serializationFlow)
              )
            }
          }
        }
      }
    }
  }
