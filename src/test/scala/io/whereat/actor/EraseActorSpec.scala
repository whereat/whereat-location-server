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
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import akka.util.Timeout
import io.whereat.db.LocationDao
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * Author: @aguestuser
 * License: GPLv2 (https://www.gnu.org/licenses/gpl-2.0.html)
 */

class EraseActorSpec(_system: ActorSystem)
  extends TestKit(_system)
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with MockFactory
  with ScalaFutures {

  implicit val timeout = Timeout(5 seconds)
  def this() = this(ActorSystem("EraseActorSpecSystem"))
  override def afterAll() = TestKit.shutdownActorSystem(system)
  val fakeDao = mock[LocationDao]

  "EraseActor" should {

    "erase the database wrapped in an Erase message" in {

      fakeDao.erase _ expects() returning Future.successful(1)
      val eraseActorRef = TestActorRef(new EraseActor)

      val res = eraseActorRef ? Erase(fakeDao)
      res.futureValue shouldEqual 1
    }
  }
}

