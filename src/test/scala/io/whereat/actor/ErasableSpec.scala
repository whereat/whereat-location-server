/**
 *
 * Copyright (c) 2015-present, Total Location Test Paragraph.
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
 *
 */

package io.whereat.actor

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import akka.util.Timeout
import io.whereat.db.LocationDao
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

/**
 * Author: @aguestuser
 * License: GPLv2 (https://www.gnu.org/licenses/gpl-2.0.html)
 */

class ErasableSpec(_system: ActorSystem)
  extends TestKit(_system)
  with Erasable
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with MockFactory {

  def this() = this(ActorSystem("EraseActorSpecSystem"))
  override def afterAll() = TestKit.shutdownActorSystem(system)

  implicit val timeout = Timeout(5 seconds)
  val fakeDao = mock[LocationDao]

  "Erasable" when {

    "#scheduleErase called" should {

      "schedule periodic erasures of the DB" in {

        val interval = 200 milli
        val offset = 10 milli
        val eraseActorRef = TestActorRef(new EraseActor)

        scheduleErase(system, testActor, fakeDao, interval)

        expectMsg(interval, Erase(fakeDao))
        expectMsgAllOf(2 * interval + offset, Erase(fakeDao), Erase(fakeDao))
        expectMsgAllOf(3 * interval + offset, Erase(fakeDao), Erase(fakeDao), Erase(fakeDao))
      }
    }
  }
}
