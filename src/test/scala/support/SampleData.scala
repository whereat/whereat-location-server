package support

import model.Location
import spray.json._
import model.LocationJsonProtocol._

/**
 * Author: @aguestuser
 * Date: 7/10/15
 * License: GPLv2 (https://www.gnu.org/licenses/gpl-2.0.html)
 */

object SampleData {

  val s17 = Location(
    id = "75782cd4-1a42-4af1-9130-05c63b2aa9ff",
    lat = 40.7092529,
    lon = -74.0112551,
    time = 1505606400000L
  )

  val s17Json =
    """{"id":"75782cd4-1a42-4af1-9130-05c63b2aa9ff","lat": 40.7092529,"lon": -74.0112551,"time": 1505606400000 }"""

  val n17 = Location(
    id = "8d3f4369-e829-4ca5-8d9b-123264aeb469",
    lat = 40.706877,
    lon = -74.0112654,
    time = 1510876800000L
  )

  val n17Json = n17.toJson.compactPrint

}
