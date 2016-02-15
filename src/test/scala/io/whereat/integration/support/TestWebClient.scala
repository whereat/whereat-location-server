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
