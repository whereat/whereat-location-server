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

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.testkit.TestProbe
import io.whereat.actor.{DispatchActor, Subscribe, Unsubscribe}
import io.whereat.model.{Error, ExpiringLocation, JsonProtocols}
import akka.stream._
import akka.stream.scaladsl.{Keep, _}
import akka.stream.testkit.TestPublisher.Probe
import akka.stream.testkit.TestSubscriber
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{ShouldMatchers, WordSpec}
import spray.json._

import scala.collection.immutable.Seq
import scala.util.{Failure, Success, Try}

class RelayFlowsSpec extends WordSpec with JsonProtocols with ShouldMatchers with MockFactory {
  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val dispatchActor: ActorRef = actorSystem.actorOf(Props[DispatchActor])
  private val TestLocation = ExpiringLocation(lat = 25.197, lon = 55.274, ttl = 5)

  "The deserialization flow" should {
    "return a Success(location) on valid location JSON" in {
      val (pub: Probe[Message], sub: TestSubscriber.Probe[Try[ExpiringLocation]]) = TestSource.probe[Message]
        .via(RelayFlows.deserializationFlow)
        .toMat(TestSink.probe[Try[ExpiringLocation]])(Keep.both)
        .run()
      val locationJson: String = TestLocation.toJson.toString

      sub.request(1)
      pub.sendNext(TextMessage.Strict(locationJson))
      sub.expectNext(Success(TestLocation))
    }

    "return a Failure on invalid JSON" in {
      val (pub: Probe[Message], sub: TestSubscriber.Probe[Try[ExpiringLocation]]) = TestSource.probe[Message]
        .via(RelayFlows.deserializationFlow)
        .toMat(TestSink.probe[Try[ExpiringLocation]])(Keep.both)
        .run()

      val invalidJson: String = "Not JSON"

      sub.request(1)
      pub.sendNext(TextMessage.Strict(invalidJson))
      sub.expectNext() shouldBe a[Failure[_]]
    }

    "return a Failure on non-location JSON" in {
      val (pub: Probe[Message], sub: TestSubscriber.Probe[Try[ExpiringLocation]]) = TestSource.probe[Message]
        .via(RelayFlows.deserializationFlow)
        .toMat(TestSink.probe[Try[ExpiringLocation]])(Keep.both)
        .run()

      val nonLocationJson: String = "[\"Not a location\"]"

      sub.request(1)
      pub.sendNext(TextMessage.Strict(nonLocationJson))
      sub.expectNext() shouldBe a[Failure[_]]
    }
  }

  "The error handling flow" should {
    "pass valid locations on to the next step" in {
      val pluggedFlow: Graph[FlowShape[Try[ExpiringLocation], ExpiringLocation], Unit] = GraphDSL.create() { implicit b =>
        import GraphDSL.Implicits._
        val handlingFlow: BidiShape[Try[ExpiringLocation], ExpiringLocation, ExpiringLocation, Either[Error, ExpiringLocation]] = b.add(RelayFlows.errorHandlingFlow)

        Source.empty[ExpiringLocation] ~> handlingFlow.in2
        handlingFlow.out2 ~> Sink.ignore

        FlowShape(handlingFlow.in1, handlingFlow.out1)
      }

      val (publisher: Probe[Try[ExpiringLocation]], subscriber: TestSubscriber.Probe[ExpiringLocation]) = TestSource.probe[Try[ExpiringLocation]]
        .via(pluggedFlow)
        .toMat(TestSink.probe[ExpiringLocation])(Keep.both)
        .run()
      subscriber.request(1)

      publisher.sendNext(Success(TestLocation))

      subscriber.expectNext(TestLocation)
    }

    "pass error messages back to the websocket" in {
      val pluggedFlow: Graph[FlowShape[Try[ExpiringLocation], Either[Error, ExpiringLocation]], Unit] = GraphDSL.create() { implicit b =>
        import GraphDSL.Implicits._
        val handlingFlow: BidiShape[Try[ExpiringLocation], ExpiringLocation, ExpiringLocation, Either[Error, ExpiringLocation]] = b.add(RelayFlows.errorHandlingFlow)

        Source.empty[ExpiringLocation] ~> handlingFlow.in2
        handlingFlow.out1 ~> Sink.ignore

        FlowShape(handlingFlow.in1, handlingFlow.out2)
      }

      val (publisher: Probe[Try[ExpiringLocation]], subscriber: TestSubscriber.Probe[Either[Error, ExpiringLocation]]) = TestSource.probe[Try[ExpiringLocation]]
        .via(pluggedFlow)
        .toMat(TestSink.probe[Either[Error, ExpiringLocation]])(Keep.both)
        .run()
      subscriber.request(1)

      publisher.sendNext(Failure(new RuntimeException))

      subscriber.expectNext(Left(Error("Invalid location")))
    }

    "pass locations coming from the dispatcher to the websocket" in {
      val pluggedFlow = GraphDSL.create() { implicit b =>
        import GraphDSL.Implicits._
        val handlingFlow: BidiShape[Try[ExpiringLocation], ExpiringLocation, ExpiringLocation, Either[Error, ExpiringLocation]] = b.add(RelayFlows.errorHandlingFlow)

        Source.empty[Try[ExpiringLocation]] ~> handlingFlow.in1
        handlingFlow.out1 ~> Sink.ignore

        FlowShape(handlingFlow.in2, handlingFlow.out2)
      }

      val (publisher: Probe[ExpiringLocation], subscriber: TestSubscriber.Probe[Either[Error, ExpiringLocation]]) = TestSource.probe[ExpiringLocation]
        .via(pluggedFlow)
        .toMat(TestSink.probe[Either[Error, ExpiringLocation]])(Keep.both)
        .run()
      subscriber.request(1)

      publisher.sendNext(TestLocation)

      subscriber.expectNext(Right(TestLocation))
    }
  }

  "The serialization flow" should {
    "serialize a location" in {
      val (pub: Probe[Either[Error, ExpiringLocation]], sub: TestSubscriber.Probe[Message]) = TestSource.probe[Either[Error, ExpiringLocation]]
        .via(RelayFlows.serializationFlow)
        .toMat(TestSink.probe[Message])(Keep.both)
        .run()

      sub.request(1)
      pub.sendNext(Right(TestLocation))
      sub.expectNext(TextMessage.Strict(TestLocation.toJson.toString))
    }

    "serialize an Error" in {
      val (pub: Probe[Either[Error, ExpiringLocation]], sub: TestSubscriber.Probe[Message]) = TestSource.probe[Either[Error, ExpiringLocation]]
        .via(RelayFlows.serializationFlow)
        .toMat(TestSink.probe[Message])(Keep.both)
        .run()

      val error = Error("Invalid location")

      sub.request(1)
      pub.sendNext(Left(error))
      sub.expectNext(TextMessage.Strict(error.toJson.toString))
    }
  }

  "The dispatch flow" should {
    "broadcast a message" in {
      val (pub: Probe[ExpiringLocation], _) = TestSource.probe[ExpiringLocation]
        .via(RelayFlows.dispatchFlow)
        .toMat(TestSink.probe[ExpiringLocation])(Keep.both)
        .run()

      val (_, sub: TestSubscriber.Probe[ExpiringLocation]) = TestSource.probe[ExpiringLocation]
        .via(RelayFlows.dispatchFlow)
        .toMat(TestSink.probe[ExpiringLocation])(Keep.both)
        .run()

      sub.request(1)
      pub.sendNext(TestLocation)
      sub.expectNext(TestLocation)
    }

    "send an unsubscribe when a connection is closed" in {

      val dispatchActor: TestProbe = TestProbe()

      val (pub: Probe[ExpiringLocation], _) = TestSource.probe[ExpiringLocation]
        .via(RelayFlows.dispatchFlow(dispatchActor.ref))
        .toMat(TestSink.probe[ExpiringLocation])(Keep.both)
        .run()

      dispatchActor.receiveN(1)

      val (pub2: Probe[ExpiringLocation], sub2: TestSubscriber.Probe[ExpiringLocation]) = TestSource.probe[ExpiringLocation]
        .via(RelayFlows.dispatchFlow(dispatchActor.ref))
        .toMat(TestSink.probe[ExpiringLocation])(Keep.both)
        .run()

      val subscribeMessages: Seq[Subscribe] = dispatchActor.receiveN(1).map(_.asInstanceOf[Subscribe])

      pub2.sendComplete() // simulates closing websocket for client 2

      dispatchActor.expectMsg(Unsubscribe(subscribeMessages.head.id))
    }
  }
}
