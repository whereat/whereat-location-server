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

package io.whereat.config

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{Matchers, WordSpecLike}
import slick.jdbc.meta.MTable

/**
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

class EnvironmentSpec extends WordSpecLike
with Matchers
with ScalaFutures {

  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  "`#of`" should {

    "recognize Prod environment" in {
      Environment.of { "PRODUCTION" } shouldEqual Prod
    }

    "recognize Dev environment" in {
      Environment.of { "DEVELOPMENT" } shouldEqual Dev
    }

    "recoginze Test environment" in {
      Environment.of { "TEST" } shouldEqual Test
    }

    "recognize NullEnv environment" in {
      Environment.of { "FOOBAR" } shouldEqual NullEnv
    }
  }

  "`#dbFor`" should {

    "initialize a remote production database" in {
      val db = Environment.dbFor(Prod)
      db.run(MTable.getTables).futureValue should not be empty

      db.shutdown.futureValue
    }

    "initialize a remote development database" in {
      val db = Environment.dbFor(Dev)
      db.run(MTable.getTables).futureValue should not be empty

      db.shutdown.futureValue
    }

    "initialize a remote test database" in {
      val db = Environment.dbFor(Test)
      db.run(MTable.getTables).futureValue should not be empty

      db.shutdown.futureValue
    }
  }
}
