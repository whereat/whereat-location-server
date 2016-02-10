package integration

import java.net.URI

import integration.support.{TestConfig, TestWebClient}
import main.MainTrait
import model.{LocationJsonProtocol, Location}
import org.scalatest.concurrent.Eventually.eventually
import org.scalatest.concurrent.ScalaFutures._
import org.scalatest.{Matchers, ShouldMatchers, WordSpec}
import spray.json._

import scala.language.postfixOps

class ClientIntegrationTest
  extends WordSpec
    with Matchers
    with ShouldMatchers
    with LocationJsonProtocol {

  object TestMain extends MainTrait with TestConfig

  "The server" should {
    "relay a location from one client to another" ignore {
      // TODO: MainTrait#run does NOT return a future that reflects the server state. This needs to be fixed
      TestMain.run.futureValue

      val serverUri: URI = new URI(s"ws://${TestMain.httpInterface}:${TestMain.httpPort}/locations/websocket")

      val clientA: TestWebClient = new TestWebClient(serverUri)
      val clientB: TestWebClient = new TestWebClient(serverUri)
      clientA.connect()
      clientB.connect()

      val sentLocation: Location = Location(
        id = "75782cd4-1a42-4af1-9130-05c63b2aa9ff",
        lat = 40.7092529,
        lon = 40.7092529,
        time = 1505606400000L
      )
      clientA.sendMessage(sentLocation.toJson.toString)
      clientA.disconnect()

      eventually(clientB.messages.map(_.parseJson.convertTo[Location])
        should contain(sentLocation))

      clientB.disconnect()
    }
  }

}
