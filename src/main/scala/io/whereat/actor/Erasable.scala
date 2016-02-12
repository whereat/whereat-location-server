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

import akka.actor._
import io.whereat.db.LocationDao

import scala.concurrent.duration._


trait Erasable {
    def scheduleErase[D <: LocationDao](sys: ActorSystem, actor: ActorRef, dao: D, dur: FiniteDuration) : Cancellable = {
      import sys.dispatcher
      sys.scheduler.schedule(0 millis, dur, actor, Erase(dao))
    }
}
