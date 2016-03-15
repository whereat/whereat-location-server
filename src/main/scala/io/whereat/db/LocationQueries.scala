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

package io.whereat.db

import io.whereat.model.Location
import slick.lifted.TableQuery
import slick.driver.H2Driver.api._

trait LocationQueries {

  implicit val ec = scala.concurrent.ExecutionContext.global

  val locations = TableQuery[Locations]

  val createSchema = locations.schema.create
  val dropSchema = locations.schema.drop

  val insert = { l: Location ⇒ locations += l }
  val insertMany = { ls: Seq[Location] ⇒ locations ++= ls }

  val fetch = { id: String ⇒ locations.filter(_.id === id) }
  val allSince = { t: Long ⇒ locations.filter(_.time > t) }

  val update = { (id: String, l: Location) ⇒ fetch(id).update(l) }

  val delete = { id: String ⇒ fetch(id).delete }
  val clear = locations.delete

  val save = { l: Location ⇒
    fetch(l.id).exists.result.flatMap {
      if(_) update(l.id, l) else insert(l)
    }.transactionally
  }
}
