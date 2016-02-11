package io.whereat.integration.support

import java.net.URI
import javax.websocket._

import scala.collection.mutable.ListBuffer

@ClientEndpoint
class TestWebClient(private var endpoint: URI) {
  private val container : WebSocketContainer = ContainerProvider.getWebSocketContainer
  private var session: Session = _

  var messages: ListBuffer[String] = ListBuffer[String]()

  def connect(): Unit = {
    this.session = container.connectToServer(this, endpoint)
  }

  @OnMessage
  def onMessage(message: String): Unit = {
    messages += message
  }

  def sendMessage(message: String): Unit = {
    session.getBasicRemote.sendText(message)
  }

  def disconnect(): Unit = {
    session.close()
  }
}
