package db

import model.Location
import slick.lifted.{Tag, ProvenShape}
import slick.driver.H2Driver.api._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.global


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

  val fetch = { id: String ⇒ locations.filter(_.id === id) }
  val allSince = { t: Long ⇒ locations.filter(_.time > t) }

  val clear = locations.delete
}

trait LocationDao extends LocationQueries {

  implicit val ec = scala.concurrent.ExecutionContext.global
  val db = Database.forConfig("devDb")

  def recordInit(loc: Location): Future[Seq[Location]] =
    db.run {
      for {
        _ ← createSchema
        l ← insert(loc)
        ls ← locations.result
      } yield ls
    }

  def recordRefresh(loc: Location, lastPing: Long): Future[Seq[Location]] =
    db.run {
      for {
        _ ← createSchema
        l ← insert(loc)
        ls ← allSince(lastPing).result
      } yield ls
    }

  //TODO figure out error-handling here (Option[Seq[Location]] didn't work)
}

object LocationDao extends LocationDao