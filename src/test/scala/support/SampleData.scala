package support

import model.{User, JsonProtocols, Location}

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

object SampleData extends JsonProtocols {

  val s17 = Location(
    id = "75782cd4-1a42-4af1-9130-05c63b2aa9ff",
    lat = 40.7092529,
    lon = -74.0112551,
    time = 1505606400000L
  )

  val s17User = User(s17.id)

  val s17UserJson = """{"id": "75782cd4-1a42-4af1-9130-05c63b2aa9ff" }"""

  val s17_ = Location(
    id = s17.id,
    lat = 40.7092530,
    lon = -74.0112552,
    time = 1505606400001L
  )

  val s17Json =
    """{
      |  "id": "75782cd4-1a42-4af1-9130-05c63b2aa9ff",
      |  "lat": 40.7092529,
      |  "lon": -74.0112551,
      |  "time": 1505606400000
      |}""".stripMargin

  val s17Json_missingField =
    """{
      |  "id": "75782cd4-1a42-4af1-9130-05c63b2aa9ff",
      |  "lat": 40.7092529,
      |  "lon": -74.0112551
      |}""".stripMargin

  val s17Json_wrongOrder =
    """{
      |  "lat": 40.7092529,
      |  "id": "75782cd4-1a42-4af1-9130-05c63b2aa9ff",
      |  "lon": -74.0112551,
      |  "time": 1505606400000
      |}""".stripMargin

  val s17Json_typeError =
    """{
      |  "id": "75782cd4-1a42-4af1-9130-05c63b2aa9ff",
      |  "lat": "40.7092529",
      |  "lon": -74.0112551,
      |  "time": 1505606400000
      |}""".stripMargin

  val s17Json_badJson =
    """{
      |  "id": "75782cd4-1a42-4af1-9130-05c63b2aa9ff",
      |  lat: "40.7092529",
      |  "lon": -74.0112551,
      |  "time": 1505606400000
      |}""".stripMargin

  val s17JsonCompact = """{"id":"75782cd4-1a42-4af1-9130-05c63b2aa9ff","lat": 40.7092529,"lon": -74.0112551,"time": 1505606400000 }"""


  val wrappedS17Json =
    """{
      |  "location": {
      |    "id": "75782cd4-1a42-4af1-9130-05c63b2aa9ff",
      |    "lat": 40.7092529,
      |    "lon": -74.0112551,
      |    "time": 1505606400000
      |  },
      |  "lastPing": 1505606400000
      |}""".stripMargin

  val wrappedS17Json_noLastPing =
    """{
      |  "location": {
      |    "id": "75782cd4-1a42-4af1-9130-05c63b2aa9ff",
      |    "lat": 40.7092529,
      |    "lon": -74.0112551,
      |    "time": 1505606400000
      |  }
      |}""".stripMargin

  val n17 = Location(
    id = "8d3f4369-e829-4ca5-8d9b-123264aeb469",
    lat = 40.706877,
    lon = -74.0112654,
    time = 1510876800000L
  )

  val n17_ = Location(
    id = "8d3f4369-e829-4ca5-8d9b-123264aeb469",
    lat = 40.706877,
    lon = -74.0112654,
    time = 1510876800001L
  )

  val n17Json =
    """{
      |  "id":"8d3f4369-e829-4ca5-8d9b-123264aeb469",
      |  "lat":40.706877,"lon":-74.0112654,
      |  "time":1510876800000
      |}""".stripMargin

  val n17JsonCompact = """{"id":"8d3f4369-e829-4ca5-8d9b-123264aeb469","lat":40.706877,"lon":-74.0112654,"time":1510876800000}"""


  val n17ReqInit =
    """{
      |  "lastPing": -1,
      |  "location": {
      |    "id": "8d3f4369-e829-4ca5-8d9b-123264aeb469",
      |    "lat": 40.706877,
      |    "lon": -74.0112654,
      |    "time": 1510876800000
      |  }
      |}""".stripMargin

    val n17ReqWrongOrder =
    """{
      |  "lastPing": -1,
      |  "location": {
      |    "lat": 40.706877,
      |    "id": "8d3f4369-e829-4ca5-8d9b-123264aeb469",
      |    "lon": -74.0112654,
      |    "time": 1510876800000
      |  }
      |}""".stripMargin

    val n17ReqMissingField =
    """{
      |  "lastPing": -1,
      |  "location": {
      |    "lat": 40.706877,
      |    "lon": -74.0112654,
      |    "time": 1510876800000
      |  }
      |}""".stripMargin


    val n17ReqTypeError =
    """{
      |  "lastPing": -1,
      |  "location": {
      |    "lat": "40.706877",
      |    "lon": -74.0112654,
      |    "time": 1510876800000
      |  }
      |}""".stripMargin


  val n17ReqRefreshLatest =
    """{
      |  "lastPing": 1510876800000,
      |  "location": {
      |    "id": "8d3f4369-e829-4ca5-8d9b-123264aeb469",
      |    "lat": 40.706877,
      |    "lon": -74.0112654,
      |    "time": 1510876800001
      |  }
      |}""".stripMargin

  val n17ReqRefreshNotLatest =
    """{
      |  "lastPing": 200,
      |  "location": {
      |    "id":"8d3f4369-e829-4ca5-8d9b-123264aeb469",
      |    "lat":40.706877,"lon":-74.0112654,
      |    "time":1510876800001
      |  }
      |}""".stripMargin

  val n17ResponseInit =
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

  val n17ResponseRefreshLatest =
    """[{
      |  "id": "8d3f4369-e829-4ca5-8d9b-123264aeb469",
      |  "lat": 40.706877,
      |  "lon": -74.0112654,
      |  "time": 1510876800001
      |}]""".stripMargin


  val n17ResponseRefreshNotLatest =
    """[{
      |  "id": "75782cd4-1a42-4af1-9130-05c63b2aa9ff",
      |  "lat": 40.7092529,
      |  "lon": -74.0112551,
      |  "time": 1505606400000
      |}, {
      |  "id": "8d3f4369-e829-4ca5-8d9b-123264aeb469",
      |  "lat": 40.706877,
      |  "lon": -74.0112654,
      |  "time": 1510876800001
      |}]""".stripMargin

  val s17n17JsonSeq =
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

  val n17JsonSeq =
    """[{
      |  "id": "8d3f4369-e829-4ca5-8d9b-123264aeb469",
      |  "lat": 40.706877,
      |  "lon": -74.0112654,
      |  "time": 1510876800000
      |}]""".stripMargin

  val march = Seq(
    Seq(
      Location(id = "id0", lat =  40.7092390, lon = -74.0112448, time = 0L),
      Location(id = "id1", lat =  40.7092391, lon = -74.0112449, time = 0L),
      Location(id = "id2", lat =  40.7092392, lon = -74.0112450, time = 0L)
    ),
    Seq(
      Location( id = "id0", lat = 40.7091984, lon = -74.0106868, time = 5000L),
      Location( id = "id1", lat = 40.7091985, lon = -74.0106869, time = 5000L),
      Location( id = "id2", lat = 40.7091986, lon = -74.0106868, time = 5000L)
    ),
    Seq(
      Location( id = "id0", lat = 40.7089950, lon = -74.0101612, time = 10000L),
      Location( id = "id1", lat = 40.7089951, lon = -74.0101613, time = 10000L),
      Location( id = "id2", lat = 40.7089952, lon = -74.0101614, time = 10000L)
    )
  )

  val growingMarch = Seq(
    Seq(
      Location(id = "id0", lat =  40.7092390, lon = -74.0112448, time = 0L)
    ),
    Seq(
      Location( id = "id0", lat = 40.7091984, lon = -74.0106868, time = 5000L),
      Location( id = "id1", lat = 40.7091985, lon = -74.0106869, time = 5000L)
    ),
    Seq(
      Location( id = "id0", lat = 40.7089950, lon = -74.0101612, time = 10000L),
      Location( id = "id1", lat = 40.7089951, lon = -74.0101613, time = 10000L),
      Location( id = "id2", lat = 40.7089952, lon = -74.0101614, time = 10000L)
    )
  )

  val crowd = Seq(
    Location( id = "id0", lat = 40.7089950, lon = -74.0101612, time = 0L),
    Location( id = "id1", lat = 40.7089951, lon = -74.0101613, time = 5000L),
    Location( id = "id2", lat = 40.7089952, lon = -74.0101614, time = 10000L),
    Location( id = "id3", lat = 40.7089953, lon = -74.0101615, time = 15000L),
    Location( id = "id4", lat = 40.7089954, lon = -74.0101616, time = 20000L),
    Location( id = "id5", lat = 40.7089955, lon = -74.0101617, time = 25000L),
    Location( id = "id6", lat = 40.7089956, lon = -74.0101618, time = 30000L)
  )



}
