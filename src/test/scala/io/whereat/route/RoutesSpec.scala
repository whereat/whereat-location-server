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

package io.whereat.route

import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.server.{Rejection, MalformedRequestContentRejection}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import io.whereat.db.LocationDao
import io.whereat.model.WrappedLocation
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import io.whereat.support.SampleData._

//import org.scalamock.scalatest.proxy.MockFactory


import scala.concurrent.Future

/**
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

class RoutesSpec extends WordSpec
with Matchers
with MockFactory
with ScalatestRouteTest
with Routes
with BeforeAndAfterEach {

  val fakeDao = mock[LocationDao]
  val rte = route(fakeDao)
  val rejectMsg = { r:Rejection ⇒ r match { case MalformedRequestContentRejection(msg,_) ⇒ msg } }

  "The API service" when {

    "receiving any request" should {

      "respond with CORS headers" in {

        Get("/hello") ~> rte ~> check {
          header("Access-Control-Allow-Origin") shouldEqual
            Some(`Access-Control-Allow-Origin`(HttpOrigin("https://demo.whereat.io")))
          header("Access-Control-Allow-Credentials") shouldEqual
            Some(`Access-Control-Allow-Credentials`(true))
          header("Access-Control-Allow-Headers") shouldEqual
            Some(`Access-Control-Allow-Headers`("Accept, Authorization", "Content-Type", "Origin", "X-Requested-With"))
        }
      }
      "respond with HPKP headers" in {
        Get("/hello") ~> rte ~> check(header("Public-Key-Pins") shouldEqual Some {
          RawHeader("Public-Key-Pins", s"""pin-sha256="$hpkpPinnedKey"; pin-sha256="$hpkpBackupKey"; pin-sha256="$hpkpEmergencyKey"; includeSubdomains; report-uri="$hpkpReportURI";max-age=$hpkpMaxAge""")
        })
      }
    }
    "receiving GET /hello" should {

      "respond with 'hello world!" in {

        Get("/hello") ~> rte ~> check {
          responseAs[String] shouldEqual "hello world!"
        }
      }
    }

    "receiving POST /locations/update" when {

      "request is well-formed" when {

        "it is user's initial location post" should {

          "respond to valid requests with list of all locations" in {

            fakeDao.put _ expects WrappedLocation(-1L, n17) returning Future.successful(Seq(s17,n17)) once()

            Post("/locations/update", HttpEntity(`application/json`, n17ReqInit)) ~> rte ~> check {
              responseAs[String] shouldEqual n17ResponseInit
            }
          }
        }

        "user has already posted" when {

          "no users have posted since user's last post" should {

            "return user's last location" in {
              fakeDao.put _ expects WrappedLocation(n17.time, n17_) returning Future.successful(Seq(n17_)) once()

              Post("/locations/update", HttpEntity(`application/json`, n17ReqRefreshLatest)) ~> rte ~> check {
                responseAs[String] shouldEqual n17ResponseRefreshLatest
              }
            }
          }

          "other users have posted since user's last post" should {

            "return all locations posted since user's last post" in {

              fakeDao.put _ expects WrappedLocation(200L, n17_) returning Future.successful(Seq(s17,n17_)) once()

              Post("/locations/update", HttpEntity(`application/json`, n17ReqRefreshNotLatest)) ~> rte ~> check {
                responseAs[String] shouldEqual n17ResponseRefreshNotLatest
              }
            }
          }
        }
      }

      "request is not well-formed" should {

        "handle requests with incorrectly ordered fields" in {

          fakeDao.put _ expects WrappedLocation(-1L, n17) returning Future.successful(Seq(s17, n17)) once()

          Post("/locations/update", HttpEntity(`application/json`, n17ReqWrongOrder)) ~> rte ~> check {
            responseAs[String] shouldEqual n17ResponseInit
          }
        }

        "reject requests with missing fields" in {

          fakeDao.put _ expects * never()

          Post("/locations/update", HttpEntity(`application/json`, n17ReqMissingField)) ~> rte ~> check {

            rejection shouldBe a[MalformedRequestContentRejection]
            rejection match {
              case MalformedRequestContentRejection(msg, _) ⇒
                msg shouldEqual "Object is missing required member 'id'"
            }
          }
        }

        "reject requests with type errors" in {

          fakeDao.put _ expects * never()

          Post("/locations/update", HttpEntity(`application/json`, s17Json_typeError)) ~> rte ~> check {

            rejection shouldBe a[MalformedRequestContentRejection]
            rejectMsg(rejection)shouldEqual """Object is missing required member 'lastPing'"""
          }
        }
      }

      "reject requests with incorrectly formatted JSON" in {

        fakeDao.put _ expects * never()

        Post("/locations/update", HttpEntity(`application/json`, s17Json_badJson)) ~> rte ~> check {

          rejection shouldBe a[MalformedRequestContentRejection]
          rejectMsg(rejection) should include("Unexpected character")
        }
      }
    }


    "receiving POST /locations/erase" should {

      "respond with notification that DB has been erased" in {

        fakeDao.erase _ expects() returning Future.successful(3)

        Post("/locations/erase") ~> rte ~> check {
          responseAs[String] shouldEqual {
          """{
             |  "msg": "Database erased. 3 record(s) deleted."
             |}""".stripMargin
          }
        }
      }
    }
  }
}
