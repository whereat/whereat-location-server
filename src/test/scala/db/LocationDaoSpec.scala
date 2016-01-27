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

import model.WrappedLocation
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest._
import slick.driver.H2Driver.api._
import support.SampleData.{n17, s17}

  /**
   * NOTE: the tests in this bracket require the following
   * environment variables to be defined in order to pass:
   *
   *   WHEREAT_DEV_DATABASE_URL
   *   WHEREAT_PROD_DATABASE_URL
   *   WHEREAT_TEST_DATABASE_URL_1
   *   WHEREAT_TEST_DATABASE_URL_2
   *
   * The variables should refer to valide remote JDBC databases
   * and must have the following query string appended to the URL:
   *
   * `?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory`
   */

class LocationDaoSpec
  extends WordSpec
  with LocationQueries
  with Matchers
  with BeforeAndAfter
  with ScalaFutures {

  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))
  implicit val ec_ = scala.concurrent.ExecutionContext.global
  var dao: LocationDaoImpl = _

  before {
    dao = LocationDaoImpl(Database.forConfig("db.test2"))
    dao.db.run {
      for {
        _ ← createSchema
        _ ← insert(s17)
      } yield {}
    }.futureValue
  }

  after {
    dao.db.run(dropSchema).futureValue
    dao.db.close()
  }

  "The Location DAO" when {

    "app loading" when {


      "a Locations schema doesn't exist" should {

        "detect no schema" in {
          dao.db.run(dropSchema).futureValue
          dao.hasSchema.futureValue shouldEqual false

          dao.db.run(createSchema).futureValue // <-- to accomodate teardown
        }

        "create a schema" in {
          dao.db.run(dropSchema).futureValue
          dao.build.futureValue shouldEqual true
        }
      }

      "a Locations schema exists" should {

        "detect a schema" in {
          dao.hasSchema.futureValue shouldEqual true
        }

        "not create a schema" in {
          dao.build.futureValue shouldEqual false
        }
      }
    }

    "handling a `put` request" when {

      "it is a user's initial location post" should {

        "insert the location and return all locations in the DB" in {
          dao.put(WrappedLocation(lastPing = -1L, location = n17))
            .futureValue shouldEqual Seq(s17, n17)
        }
      }

      "the user has already posted a location" when {

        "no users have posted since user's last post" should {

          "update the user's location and return it" in {
            dao.db.run(insert(n17)).futureValue
            dao.put(WrappedLocation(s17.time,n17)).futureValue shouldEqual Seq(n17)
          }
        }

        "other users have posted since user's last post" should {

          "update the user's location and return all locations posted since user's last post" in {
            dao.db.run(insert(n17)).futureValue
            dao.put(WrappedLocation(-1L,n17)).futureValue shouldEqual Seq(s17,n17)
          }
        }
      }
    }

    "handling a remove request" should {

      "delete an existing resource" in {
        dao.remove(s17.id).futureValue shouldEqual 1
      }

      "not delete a non-existent resource" in {
        dao.remove("hi_there!").futureValue shouldEqual 0
      }
    }

    "handling an erase request" should {

      "delete all rows in the locations table" in {
        dao.db.run(insert(n17)).futureValue
        dao.erase.futureValue shouldEqual 2
        dao.db.run(locations.size.result).futureValue shouldEqual 0
      }
    }
  }
}
