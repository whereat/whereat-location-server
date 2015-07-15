package db

import model.Location
import slick.lifted.{Tag, ProvenShape}
import slick.driver.H2Driver.api._


/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

class Locations(tag: Tag) extends Table[Location](tag, "LOCATIONS") {

  def id = column[String]("ID", O.PrimaryKey)
  def lat = column[Double]("LAT")
  def lon = column[Double]("LON")
  def time = column[Long]("TIME")

  def *  = (id, lat, lon, time) <> ((Location.apply _).tupled, Location.unapply)
  def idx = index("idx_time", time, unique = false)
}

trait LocationQueries {
  val locations = TableQuery[Locations]
  val createSchema = locations.schema.create

  val insert = { l: Location ⇒ locations += l }
  val insertMany = { ls: Seq[Location] ⇒ locations ++= ls }

  val get = { id: String ⇒ locations.filter(_.id === id) }
  val allSince = { t: Long ⇒ locations.filter(_.time > t) }

  val clear = locations.delete
}