package db

import model.{WrappedLocation, Location}
import slick.driver.H2Driver.api._
import slick.jdbc.meta.MTable
import scala.concurrent.Future

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

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

  def getSince(t: Long): Future[Seq[Location]] =
    db.run(allSince(t).result)

  def remove(id: String): Future[Int] = db.run { delete(id) }

  def erase: Future[Int] = db.run { clear }

  //TODO test sad path / error handling
}

case class LocationDaoImpl(db: Database) extends LocationDao