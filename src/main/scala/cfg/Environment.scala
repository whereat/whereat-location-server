package cfg

import slick.driver.PostgresDriver
import PostgresDriver.api.Database
import PostgresDriver.backend.DatabaseDef

/**
 * Author: @aguestuser
 * Date: 10/28/15
 * License: GPLv2 (https://www.gnu.org/licenses/gpl-2.0.html)
 */

trait Environment
case object Prod extends Environment
case object Dev extends Environment
case object Test extends Environment
case object NullEnv extends Environment

object Environment {

  type Backend = slick.backend.DatabaseComponent

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