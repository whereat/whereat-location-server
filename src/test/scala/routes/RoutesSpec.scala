package routes

import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.{Rejection, MalformedRequestContentRejection}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import db.LocationDao
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

    "receiving GET /hello" should {

      "respond with 'hello world!" in {

        Get("/hello") ~> rte ~> check {
          responseAs[String] shouldEqual "hello world!"
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

        fakeDao.refresh _ expects(s17, s17.time) returning Future.successful(Seq(n17)) once()

        Post("/locations/refresh", HttpEntity(`application/json`, wrappedS17Json)) ~> rte ~> check {
          responseAs[String] shouldEqual n17JsonSeq
        }
      }

      "reject requests that send Location JSON instead of WrappedLocation" in {

        fakeDao.init _ expects * never()

        Post("/locations/refresh", HttpEntity(`application/json`, s17Json)) ~> rte ~> check {
          rejection shouldBe a[MalformedRequestContentRejection]
          rejectMsg(rejection) should include ("Object is missing required member 'location'")
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
    
    "receiving POST /locations/erase" should {

      "respond with notification that DB has been erased" in {

        fakeDao.erase _ expects() returning Future.successful("Database erased.")

        Post("/locations/erase") ~> rte ~> check {
          responseAs[String] shouldEqual "Database erased."
        }
      }
    }
  }
}
