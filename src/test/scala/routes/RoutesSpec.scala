package routes

import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.{MalformedRequestContentRejection, Rejection, Route}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import db.LocationDao
import model.Location
import org.scalatest.{Matchers, WordSpec}
import support.SampleData._

import scala.concurrent.Future

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

class RoutesSpec extends WordSpec with Matchers with ScalatestRouteTest with Routes {

  object FakeLocationDao extends LocationDao {
    override def recordInit(loc: Location) =
      Future.successful(Seq(s17, n17))

    override def recordRefresh(loc: Location, lastPing: Long) =
      Future.successful(Seq(n17))
  }

  val rte = route[LocationDao](FakeLocationDao)

  "The service" should {

    "respond to GET/hello with 'hello world!'" in {

      Get("/hello") ~> rte ~> check {
        responseAs[String] shouldEqual "hello world!"
      }

    }

    "respond to valid POST/locations/init requests with all locations in DB" in {

      Post("/locations/init", HttpEntity(`application/json`, s17Json)) ~> rte ~> check {
        responseAs[String] shouldEqual s17n17JsonSeq
      }

    }

    "respond to POST/locations/init requests with incorrectly ordered fields" in {

      Post("/locations/init", HttpEntity(`application/json`, s17Json)) ~> rte ~> check {
        responseAs[String] shouldEqual s17n17JsonSeq
      }

    }

    "reject POST/locations/init requests with missing fields" in {

      Post("/locations/init", HttpEntity(`application/json`, s17Json_missingField)) ~> rte ~> check {

        rejection shouldBe a[MalformedRequestContentRejection]
        rejection match {
          case MalformedRequestContentRejection(msg, _) ⇒
            msg shouldEqual "Object is missing required member 'time'"
        }
      }
    }

    "reject POST/locations/init requests with type errors" in {

      Post("/locations/init", HttpEntity(`application/json`, s17Json_typeError)) ~> rte ~> check {

        rejection shouldBe a[MalformedRequestContentRejection]
        rejection match {
          case MalformedRequestContentRejection(msg, _) ⇒
            msg shouldEqual """Expected Double as JsNumber, but got "40.7092529""""
        }
      }
    }

    "reject POST/locations/init requests with incorrectly formatted JSON" in {

      Post("/locations/init", HttpEntity(`application/json`, s17Json_badJson)) ~> rte ~> check {

        rejection shouldBe a[MalformedRequestContentRejection]
        rejection match {
          case MalformedRequestContentRejection(msg, _) ⇒
            msg should include("Unexpected character")
        }
      }
    }

    "respond to valid POST/locations/refresh requests with all locations since last ping" in {
      Post("/locations/refresh", HttpEntity(`application/json`, wrappedS17Json)) ~> rte ~> check {
        responseAs[String] shouldEqual n17JsonSeq
      }
    }

  }

}
