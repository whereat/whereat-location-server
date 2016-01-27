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

import model.Location
import slick.lifted.Tag
import slick.driver.PostgresDriver.api._


class Locations(tag: Tag) extends Table[Location](tag, "LOCATIONS") {

  def id = column[String]("ID", O.PrimaryKey)
  def lat = column[Double]("LAT")
  def lon = column[Double]("LON")
  def time = column[Long]("TIME")

  def *  = (id, lat, lon, time) <> ((Location.apply _).tupled, Location.unapply)
  def idx = index("idx_time", time, unique = false)
}





