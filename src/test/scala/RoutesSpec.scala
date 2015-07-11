import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}
import routes.Routes
import support.SampleData

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

class RoutesSpec extends WordSpec with Matchers with ScalatestRouteTest with Routes {

  lazy val s17 = SampleData.s17
  lazy val s17Json = SampleData.s17Json

  "The service" should {

    "respond to GET/hello with 'hello world!'" in {

      Get("/hello") ~> route ~> check {
        responseAs[String] shouldEqual "hello world!"
      }

    }

    "respond to a properly formated POST/locations with echo" in {

      Post("/locations", HttpEntity(`application/json`, s17Json)) ~> route ~> check {
        responseAs[String] shouldEqual s17Json
      }

    }

  }

}
