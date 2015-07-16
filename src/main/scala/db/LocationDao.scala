package db

import model.Location
import slick.driver.H2Driver.api._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.global

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

trait LocationDao extends LocationQueries {

  implicit val ec = scala.concurrent.ExecutionContext.global
  val db: Database

  def recordInit(loc: Location): Future[Seq[Location]] =
    db.run {
      for {
        l ← insert(loc)
        ls ← locations.result
      } yield ls
    }

  def recordRefresh(loc: Location, lastPing: Long): Future[Seq[Location]] =
    db.run {
      for {
        l ← update(loc.id, loc)
        ls ← allSince(lastPing).result
      } yield ls
    }

  //TODO test sad path / error handling
}

//object LocationDao extends LocationDao {
//  val db = Database.forConfig("devDb")
//}

case class LocationDaoImpl(db: Database) extends LocationDao