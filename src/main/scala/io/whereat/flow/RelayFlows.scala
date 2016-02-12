package io.whereat.flow

import akka.http.scaladsl.model.ws
import akka.http.scaladsl.model.ws.TextMessage
import akka.http.scaladsl.model.ws.TextMessage.Strict
import akka.stream.BidiShape
import akka.stream.scaladsl._
import io.whereat.model.{Error, JsonProtocols, Location}
import spray.json._

import scala.util.{Failure, Success, Try}

object RelayFlows extends JsonProtocols {
  val errorHandlingFlow: BidiFlow[Try[Location], Location, Location, Either[Error, Location], Unit] =
    BidiFlow.fromGraph(GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._
      val in1 = b.add(Broadcast[Try[Location]](2))
      val successFlow = b.add(Flow[Try[Location]].collect {
        case Success(location) => location
      })
      val failureFlow = b.add(Flow[Try[Location]].collect {
        case Failure(_) => Left(Error("Invalid location"))
      })
      val in2 = b.add(Flow[Location].map(location => Right(location)))
      val out2 = b.add(Merge[Either[Error, Location]](2))
      val out1 = b.add(Flow[Location])

      in1 ~> successFlow ~> out1
      in1 ~> failureFlow
      out2 <~ failureFlow
      out2 <~ in2

      BidiShape(in1.in, out1.out, in2.in, out2.out)
    })

  val workingFlow = Flow[ws.Message].map(
    incomingMessage => {
      val maybeLocation: Try[Location] = Try(incomingMessage.asInstanceOf[Strict].text.parseJson.convertTo[Location])
      maybeLocation match {
        case Success(location) => incomingMessage
        case Failure(exception) => TextMessage.Strict(Error("Invalid location").toJson.toString)
      }
    }
  )

  val deserializationFlow = Flow[ws.Message].map(
    incomingMessage =>
      Try(incomingMessage.asInstanceOf[Strict].text.parseJson.convertTo[Location])
  )

  val serializationFlow = Flow[Either[Error, Location]].map {
    case Left(error) => TextMessage.Strict(error.toJson.toString)
    case Right(location) => TextMessage.Strict(location.toJson.toString)
  }
}
