package routes

import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.headers.{HttpOrigin, `Access-Control-Allow-Credentials`, `Access-Control-Allow-Headers`, `Access-Control-Allow-Origin`}
import akka.http.scaladsl.server.{MalformedRequestContentRejection, Rejection}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import db.LocationDao
import model.WrappedLocation
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import support.SampleData._

//import org.scalamock.scalatest.proxy.MockFactory


import scala.concurrent.Future

/**
 * Author: @aguestuser
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
            Some(`Access-Control-Allow-Origin`(HttpOrigin("https://whereat.io")))
          header("Access-Control-Allow-Credentials") shouldEqual
            Some(`Access-Control-Allow-Credentials`(false))
          header("Access-Control-Allow-Headers") shouldEqual
            Some(`Access-Control-Allow-Headers`(
              "Accept, Authorization", "Content-Type", "Origin", "X-Requested-With"))
        }
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

          fakeDao.init _ expects s17 returning Future.successful(Seq(s17, n17)) once()

          Post("/locations/init", HttpEntity(`application/json`, s17Json_wrongOrder)) ~> rte ~> check {
            responseAs[String] shouldEqual s17n17JsonSeq
          }
        }

        "reject requests with missing fields" in {

          fakeDao.init _ expects * never()

          Post("/locations/init", HttpEntity(`application/json`, s17Json_missingField)) ~> rte ~> check {

            rejection shouldBe a[MalformedRequestContentRejection]
            rejection match {
              case MalformedRequestContentRejection(msg, _) ⇒
                msg shouldEqual "Object is missing required member 'time'"
            }
          }
        }

        "reject requests with type errors" in {

          fakeDao.init _ expects * never()

          Post("/locations/init", HttpEntity(`application/json`, s17Json_typeError)) ~> rte ~> check {

            rejection shouldBe a[MalformedRequestContentRejection]
            rejectMsg(rejection)shouldEqual """Expected Double as JsNumber, but got "40.7092529""""
          }
        }
      }


      "reject requests with incorrectly formatted JSON" in {

        fakeDao.init _ expects * never()

        Post("/locations/init", HttpEntity(`application/json`, s17Json_badJson)) ~> rte ~> check {

          rejection shouldBe a[MalformedRequestContentRejection]
          rejectMsg(rejection) should include("Unexpected character")

        }
      }
    }

    "receiving POST /locations/init" should {

      "respond to valid requests with list of all locations" in {

        fakeDao.init _ expects s17 returning Future.successful(Seq(s17, n17)) once()

        Post("/locations/init", HttpEntity(`application/json`, s17Json)) ~> rte ~> check {
          responseAs[String] shouldEqual s17n17JsonSeq
        }
      }

      "handle requests with incorrectly ordered fields" in {

        fakeDao.init _ expects s17 returning Future.successful(Seq(s17, n17)) once()

        Post("/locations/init", HttpEntity(`application/json`, s17Json_wrongOrder)) ~> rte ~> check {
          responseAs[String] shouldEqual s17n17JsonSeq
        }
      }

      "reject requests with missing fields" in {

        fakeDao.init _ expects * never()

        Post("/locations/init", HttpEntity(`application/json`, s17Json_missingField)) ~> rte ~> check {

          rejection shouldBe a[MalformedRequestContentRejection]
          rejection match {
            case MalformedRequestContentRejection(msg, _) ⇒
              msg shouldEqual "Object is missing required member 'time'"
          }
        }
      }

      "reject requests with type errors" in {

        fakeDao.init _ expects * never()

        Post("/locations/init", HttpEntity(`application/json`, s17Json_typeError)) ~> rte ~> check {

          rejection shouldBe a[MalformedRequestContentRejection]
          rejectMsg(rejection)shouldEqual """Expected Double as JsNumber, but got "40.7092529""""
        }
      }

      "reject requests with incorrectly formatted JSON" in {

        fakeDao.init _ expects * never()

        Post("/locations/init", HttpEntity(`application/json`, s17Json_badJson)) ~> rte ~> check {

          rejection shouldBe a[MalformedRequestContentRejection]
          rejectMsg(rejection) should include("Unexpected character")

        }
      }
    }

    "receiving POST /locations/refresh" should {

      "respond to valid requests with all locations received since last ping from requesting user" in {

        fakeDao.refresh _ expects(s17.time, s17) returning Future.successful(Seq(n17)) once()

        Post("/locations/refresh", HttpEntity(`application/json`, wrappedS17Json)) ~> rte ~> check {
          responseAs[String] shouldEqual n17JsonSeq
        }
      }

      "reject requests that send Location JSON instead of WrappedLocation" in {

        fakeDao.init _ expects * never()

        Post("/locations/refresh", HttpEntity(`application/json`, s17Json)) ~> rte ~> check {
          rejection shouldBe a[MalformedRequestContentRejection]
          rejectMsg(rejection) should include ("Object is missing required member 'lastPing'")
        }
      }

      "reject requests that omit a `lastPing` field" in {

        fakeDao.init _ expects * never()

        Post("/locations/refresh", HttpEntity(`application/json`, wrappedS17Json_noLastPing)) ~> rte ~> check {
          rejection shouldBe a[MalformedRequestContentRejection]
          rejectMsg(rejection) should include ("Object is missing required member 'lastPing'")
        }
      }
    }

    "receiving POST /locations/remove" should {

      "respond to request matching existing resource with notification of its deletion" in {

        fakeDao.remove _ expects s17.id returning Future.successful(1)

        Post("/locations/remove", HttpEntity(`application/json`, s17UserJson)) ~> rte ~> check {
          responseAs[String] shouldEqual "1 record(s) deleted."
        }
      }

      "respond to request matching non-existent resource with notification of no deletions" in {

        fakeDao.remove _ expects "hi there!" returning Future.successful(0)

        Post("/locations/remove", HttpEntity(`application/json`, "hi there!")) ~> rte ~> check {
          responseAs[String] shouldEqual "0 record(s) deleted."
        }
      }
    }



    "receiving POST /locations/erase" should {

      "respond with notification that DB has been erased" in {

        fakeDao.erase _ expects() returning Future.successful(3)

        Post("/locations/erase") ~> rte ~> check {
          responseAs[String] shouldEqual "Database erased. 3 record(s) deleted."
        }
      }
    }
  }
}
