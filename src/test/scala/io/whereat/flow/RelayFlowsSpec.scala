package io.whereat.flow

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import io.whereat.model.{Error, JsonProtocols, Location}
import akka.stream._
import akka.stream.scaladsl.{Keep, _}
import akka.stream.testkit.TestPublisher.Probe
import akka.stream.testkit.TestSubscriber
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import org.scalatest.{ShouldMatchers, WordSpec}
import spray.json._

import scala.util.{Failure, Success, Try}

class RelayFlowsSpec extends WordSpec with JsonProtocols with ShouldMatchers {
  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()

  "The deserialization flow" should {
    "return a Success(location) on valid location JSON" in {
      val (pub: Probe[Message], sub: TestSubscriber.Probe[Try[Location]]) = TestSource.probe[Message]
        .via(RelayFlows.deserializationFlow)
        .toMat(TestSink.probe[Try[Location]])(Keep.both)
        .run()
      val location: Location = Location(id = "id", lat = 10L, lon = -10L, time = 0)
      val locationJson: String = location.toJson.toString

      sub.request(1)
      pub.sendNext(TextMessage.Strict(locationJson))
      sub.expectNext(Success(location))
    }

    "return a Failure on invalid JSON" in {
      val (pub: Probe[Message], sub: TestSubscriber.Probe[Try[Location]]) = TestSource.probe[Message]
        .via(RelayFlows.deserializationFlow)
        .toMat(TestSink.probe[Try[Location]])(Keep.both)
        .run()

      val invalidJson: String = "Not JSON"

      sub.request(1)
      pub.sendNext(TextMessage.Strict(invalidJson))
      sub.expectNext() shouldBe a[Failure[_]]
    }

    "return a Failure on non-location JSON" in {
      val (pub: Probe[Message], sub: TestSubscriber.Probe[Try[Location]]) = TestSource.probe[Message]
        .via(RelayFlows.deserializationFlow)
        .toMat(TestSink.probe[Try[Location]])(Keep.both)
        .run()

      val nonLocationJson: String = "[\"Not a location\"]"

      sub.request(1)
      pub.sendNext(TextMessage.Strict(nonLocationJson))
      sub.expectNext() shouldBe a[Failure[_]]
    }
  }

  "The error handling flow" should {
    "pass valid locations on to the next step" in {
      val pluggedFlow: Graph[FlowShape[Try[Location], Location], Unit] = GraphDSL.create() { implicit b =>
        import GraphDSL.Implicits._
        val handlingFlow: BidiShape[Try[Location], Location, Location, Either[Error, Location]] = b.add(RelayFlows.errorHandlingFlow)

        Source.empty[Location] ~> handlingFlow.in2
        handlingFlow.out2 ~> Sink.ignore

        FlowShape(handlingFlow.in1, handlingFlow.out1)
      }

      val location: Location = Location(id = "id", lat = 10L, lon = -10L, time = 20)

      val (publisher: Probe[Try[Location]], subscriber: TestSubscriber.Probe[Location]) = TestSource.probe[Try[Location]]
        .via(pluggedFlow)
        .toMat(TestSink.probe[Location])(Keep.both)
        .run()
      subscriber.request(1)

      publisher.sendNext(Success(location))

      subscriber.expectNext(location)
    }

    "pass error messages back to the websocket" in {
      val pluggedFlow: Graph[FlowShape[Try[Location], Either[Error, Location]], Unit] = GraphDSL.create() { implicit b =>
        import GraphDSL.Implicits._
        val handlingFlow: BidiShape[Try[Location], Location, Location, Either[Error, Location]] = b.add(RelayFlows.errorHandlingFlow)

        Source.empty[Location] ~> handlingFlow.in2
        handlingFlow.out1 ~> Sink.ignore

        FlowShape(handlingFlow.in1, handlingFlow.out2)
      }

      val location: Location = Location(id = "id", lat = 10L, lon = -10L, time = 20)

      val (publisher: Probe[Try[Location]], subscriber: TestSubscriber.Probe[Either[Error, Location]]) = TestSource.probe[Try[Location]]
        .via(pluggedFlow)
        .toMat(TestSink.probe[Either[Error, Location]])(Keep.both)
        .run()
      subscriber.request(1)

      publisher.sendNext(Failure(new RuntimeException))

      subscriber.expectNext(Left(Error("Invalid location")))
    }

    "pass locations coming from the dispatcher to the websocket" in {
      val pluggedFlow = GraphDSL.create() { implicit b =>
        import GraphDSL.Implicits._
        val handlingFlow: BidiShape[Try[Location], Location, Location, Either[Error, Location]] = b.add(RelayFlows.errorHandlingFlow)

        Source.empty[Try[Location]] ~> handlingFlow.in1
        handlingFlow.out1 ~> Sink.ignore

        FlowShape(handlingFlow.in2, handlingFlow.out2)
      }

      val location: Location = Location(id = "id", lat = 10L, lon = -10L, time = 20)

      val (publisher: Probe[Location], subscriber: TestSubscriber.Probe[Either[Error, Location]]) = TestSource.probe[Location]
        .via(pluggedFlow)
        .toMat(TestSink.probe[Either[Error, Location]])(Keep.both)
        .run()
      subscriber.request(1)

      publisher.sendNext(location)

      subscriber.expectNext(Right(location))
    }
  }

  "The serialization flow" should {
    "serialize a location" in {
      val (pub: Probe[Either[Error, Location]], sub: TestSubscriber.Probe[Message]) = TestSource.probe[Either[Error, Location]]
        .via(RelayFlows.serializationFlow)
        .toMat(TestSink.probe[Message])(Keep.both)
        .run()

      val location = Location(id="42", lat=7, lon=9, time=33333)

      sub.request(1)
      pub.sendNext(Right(location))
      sub.expectNext(TextMessage.Strict(location.toJson.toString))
    }

    "serialize an Error" in {
      val (pub: Probe[Either[Error, Location]], sub: TestSubscriber.Probe[Message]) = TestSource.probe[Either[Error, Location]]
        .via(RelayFlows.serializationFlow)
        .toMat(TestSink.probe[Message])(Keep.both)
        .run()

      val error = Error("Invalid location")

      sub.request(1)
      pub.sendNext(Left(error))
      sub.expectNext(TextMessage.Strict(error.toJson.toString))
    }
  }
}
