/*
 * Copyright (c) 2016-present, Total Location Test Paragraph.
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

package io.whereat.flow

import java.util.UUID

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.ws
import akka.http.scaladsl.model.ws.TextMessage
import akka.http.scaladsl.model.ws.TextMessage.Strict
import akka.stream.scaladsl._
import akka.stream.{BidiShape, OverflowStrategy}
import io.whereat.actor.{Unsubscribe, DispatchActor, Subscribe}
import io.whereat.model._
import spray.json._

import scala.util.{Failure, Success, Try}

object RelayFlows extends JsonProtocols {

  private implicit val system : ActorSystem = ActorSystem()

  val errorHandlingFlow: BidiFlow[Try[ExpiringLocation], ExpiringLocation, ExpiringLocation, Either[Error, ExpiringLocation], Unit] =
    BidiFlow.fromGraph(GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._
      val in1 = b.add(Broadcast[Try[ExpiringLocation]](2))
      val successFlow = b.add(Flow[Try[ExpiringLocation]].collect {
        case Success(location) => location
      })
      val failureFlow = b.add(Flow[Try[ExpiringLocation]].collect {
        case Failure(_) => Left(Error("Invalid location"))
      })
      val in2 = b.add(Flow[ExpiringLocation].map(location => Right(location)))
      val out2 = b.add(Merge[Either[Error, ExpiringLocation]](2))
      val out1 = b.add(Flow[ExpiringLocation])

      in1 ~> successFlow ~> out1
      in1 ~> failureFlow
      out2 <~ failureFlow
      out2 <~ in2

      BidiShape(in1.in, out1.out, in2.in, out2.out)
    })

  val deserializationFlow = Flow[ws.Message].map(
    incomingMessage =>
      Try(incomingMessage.asInstanceOf[Strict].text.parseJson.convertTo[ExpiringLocation])
  )

  val serializationFlow = Flow[Either[Error, ExpiringLocation]].map {
    case Left(error) => TextMessage.Strict(error.toJson.toString)
    case Right(location) => TextMessage.Strict(location.toJson.toString)
  }

  def dispatchFlow(implicit dispatchActor: ActorRef): Flow[ExpiringLocation, ExpiringLocation, Unit] = {
    val id: String = UUID.randomUUID().toString
    val source: Source[ExpiringLocation, Unit] = Source.actorRef[ExpiringLocation](1, OverflowStrategy.fail).mapMaterializedValue(
      subscriber =>
        dispatchActor ! Subscribe(id, subscriber)
    )

    val sink: Sink[Any, Unit] = Sink.actorRef(dispatchActor, Unsubscribe(id))

    Flow.fromSinkAndSource(sink, source)
  }
}
