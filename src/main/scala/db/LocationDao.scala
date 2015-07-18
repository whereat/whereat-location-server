package db

import model.Location
import slick.driver.H2Driver.api._
import scala.concurrent.Future

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

trait LocationDao extends LocationQueries {

  implicit val ec = scala.concurrent.ExecutionContext.global
  val db: Database

  def init(loc: Location): Future[Seq[Location]] =
    db.run {
      for {
        l ← insert(loc)
        ls ← locations.result
      } yield ls
    }

  def refresh(loc: Location, lastPing: Long): Future[Seq[Location]] =
    db.run {
      for {
        l ← update(loc.id, loc)
        ls ← allSince(lastPing).result
      } yield ls
    }

  def erase: Future[String] =
    db.run { for {_ ← clear } yield "Database erased." }

  //TODO test sad path / error handling
}

case class LocationDaoImpl(db: Database) extends LocationDao