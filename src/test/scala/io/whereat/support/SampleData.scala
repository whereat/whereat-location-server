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

package io.whereat.support

import io.whereat.model.{User, JsonProtocols, Location}


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

}
