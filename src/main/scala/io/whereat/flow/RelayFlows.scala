package io.whereat.flow

import akka.http.scaladsl.model.ws
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.model.ws.TextMessage.Strict
import akka.stream.{FlowShape, Graph}
import akka.stream.scaladsl.{BidiFlow, GraphDSL, Flow}
import io.whereat.model.{JsonProtocols, Error, Location}
import spray.json._

import scala.util.{Failure, Success, Try}

object RelayFlows extends JsonProtocols {
  val errorHandlingFlow: BidiFlow[Try[Location], Location, Location, Message, Unit] =
    BidiFlow.fromFlows(Flow[Try[Location]].map(_.get), Flow[Location].map(_ => TextMessage.Strict("Not what you want")))

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
}
