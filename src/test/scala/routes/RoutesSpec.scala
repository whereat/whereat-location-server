package routes

import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.testkit.ScalatestRouteTest
import db.LocationDao
import model.Location
import org.scalatest.{Matchers, WordSpec}
import support.SampleData.{s17, s17Json, n17}

import scala.concurrent.Future

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

class RoutesSpec extends WordSpec with Matchers with ScalatestRouteTest with Routes {

  object FakeLocationDao extends LocationDao {
    override def writeAndReport(loc: Location) = {
      Future.successful(Seq(s17, n17))
    }
  }

  val rte = route[LocationDao](FakeLocationDao)

  "The service" should {

    "respond to GET/hello with 'hello world!'" in {

      Get("/hello") ~> rte ~> check {
        responseAs[String] shouldEqual "hello world!"
      }

    }

    "respond to a properly formated POST/locations with echo" in {

      Post("/locations", HttpEntity(`application/json`, s17Json)) ~> rte ~> check {
        responseAs[String] shouldEqual
          """[{
            |  "id": "75782cd4-1a42-4af1-9130-05c63b2aa9ff",
            |  "lat": 40.7092529,
            |  "lon": -74.0112551,
            |  "time": 1505606400000
            |}, {
            |  "id": "8d3f4369-e829-4ca5-8d9b-123264aeb469",
            |  "lat": 40.706877,
            |  "lon": -74.0112654,
            |  "time": 1510876800000
            |}]""".stripMargin
      }

    }

  }

}
