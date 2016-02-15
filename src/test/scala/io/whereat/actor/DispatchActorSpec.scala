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

import akka.actor.{Terminated, Props, ActorSystem}
import akka.testkit.TestProbe
import io.whereat.model.JsonProtocols
import org.scalatest.{ShouldMatchers, WordSpec}

class DispatchActorSpec extends WordSpec with JsonProtocols with ShouldMatchers {
  implicit val actorSystem = ActorSystem()
  val dispatchActor = actorSystem.actorOf(Props[DispatchActor], "SeanPenn")

  "The dispatch actor" should {
    "broadcast a message to an actor after it subscribes and sends a message" in {
      val probe = TestProbe()
      dispatchActor ! Subscribe(probe.ref)
      dispatchActor ! "hello"
      probe.expectMsg("hello")
    }

    "broadcast a message to all actors after they subscribe and a message is sent" in {
      val probe1 = TestProbe()
      val probe2 = TestProbe()
      dispatchActor ! Subscribe(probe1.ref)
      dispatchActor ! Subscribe(probe2.ref)
      dispatchActor ! "hello"
      probe1.expectMsg("hello")
      probe2.expectMsg("hello")
    }

    "not broadcast a message to a subscribed actor after it unsubscribes" in {
      val probe1 = TestProbe()
      val probe2 = TestProbe()
      dispatchActor ! Subscribe(probe1.ref)
      dispatchActor ! Subscribe(probe2.ref)
      dispatchActor ! Unsubscribe(probe1.ref)
      dispatchActor ! "hello"
      probe1.expectNoMsg()
      probe2.expectMsg("hello")
    }
  }
}
