package model

import org.scalatest.{Matchers, WordSpec}
import spray.json._

/**
 * Author: @aguestuser
 * Date: 9/7/15
 * License: GPLv2 (https://www.gnu.org/licenses/gpl-2.0.html)
 */
class MessageSpec
  extends WordSpec
  with Matchers
  with JsonProtocols {

  "Message" should {

    "serialize to JSON" in {
      Message("hi!").toJson.toString() shouldEqual """{"msg":"hi!"}"""
    }

    "deserialize from JSON" in {

      """{"msg":"hi!"}""".parseJson.convertTo[Message] shouldEqual Message("hi!")
    }
  }
}
