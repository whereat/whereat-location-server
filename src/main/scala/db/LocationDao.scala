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

package db

import model.{WrappedLocation, Location}
import slick.driver.H2Driver.api._
import slick.jdbc.meta.MTable
import scala.concurrent.Future


trait LocationDao extends LocationQueries {

  val db: Database

  def hasSchema : Future[Boolean] =
    db.run { MTable.getTables } map { ts ⇒
      ts.exists { _.name.name == "LOCATIONS" }
    }

  def build : Future[Boolean] = hasSchema flatMap { exists ⇒
    if (!exists) db.run { createSchema } flatMap { _ ⇒ Future.successful(true) }
    else Future.successful { false }
  }

  def put(wl: WrappedLocation): Future[Seq[Location]] =
    db.run {
      for {
        l ← save(wl.location)
        ls ← allSince(wl.lastPing).result
      } yield ls
    }

  def remove(id: String): Future[Int] = db.run { delete(id) }

  def erase: Future[Int] = db.run { clear }

  //TODO test sad path / error handling
}

case class LocationDaoImpl(db: Database) extends LocationDao
