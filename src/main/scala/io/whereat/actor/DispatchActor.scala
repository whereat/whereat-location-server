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

package io.whereat.actor

import akka.actor.{Actor, ActorRef}

case class Subscribe(subscriber: ActorRef)
case class Unsubscribe(subscriber: ActorRef)

class DispatchActor extends Actor {

  var subscribers = Set.empty[ActorRef]

  def broadcast(msg: Any): Unit = subscribers.foreach(_ ! msg)

  override def receive = {
    case Subscribe(subscriber) =>
      subscribers += subscriber
    case Unsubscribe(subscriber) =>
      subscribers -= subscriber
    case msg =>
      broadcast(msg)
  }
}
