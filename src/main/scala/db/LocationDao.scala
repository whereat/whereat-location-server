package db

import model.{WrappedLocation, Location}
import slick.driver.H2Driver.api._
import scala.concurrent.Future

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

trait LocationDao extends LocationQueries {

  val db: Database

  def build : Future[Unit] = db.run { createSchema }

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