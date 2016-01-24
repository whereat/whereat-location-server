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

package cfg

import slick.driver.PostgresDriver
import PostgresDriver.api.Database
import PostgresDriver.backend.DatabaseDef


trait Environment
case object Prod extends Environment
case object Dev extends Environment
case object Test extends Environment
case object NullEnv extends Environment

object Environment {

  def of(sVar: String): Environment = sVar match {
    case "PRODUCTION" ⇒ Prod
    case "DEVELOPMENT" ⇒ Dev
    case "TEST" ⇒ Test
    case _ ⇒ NullEnv
  }

  def dbFor(env: Environment): DatabaseDef = env match {
    case Prod ⇒ Database.forConfig("db.prod")
    case Dev ⇒ Database.forConfig("db.dev")
    case Test ⇒ Database.forConfig("db.test")
    case NullEnv ⇒ Database.forConfig("db.test")
  }
}
