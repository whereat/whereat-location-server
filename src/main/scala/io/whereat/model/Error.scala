package io.whereat.model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

case class Error(error: String)
object Error

trait ErrorJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val errorFormat = jsonFormat1(Error.apply)
}
