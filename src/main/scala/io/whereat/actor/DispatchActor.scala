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

case class Subscribe(id: String, subscriber: ActorRef)
case class Unsubscribe(id: String)

class DispatchActor extends Actor {

  var subscribers: Map[String, ActorRef] = Map.empty[String, ActorRef]

  def broadcast(msg: Any): Unit = subscribers.foreach(_._2 ! msg)

  override def receive = {
    case Subscribe(id, subscriber) =>
      subscribers += (id -> subscriber)
    case Unsubscribe(id) =>
      subscribers -= id
    case msg =>
      broadcast(msg)
  }
}
