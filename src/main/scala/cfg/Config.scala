package cfg

import com.typesafe.config.ConfigFactory
import slick.driver.PostgresDriver.api.{Database ⇒ PGDatabase}

import scala.util.Properties


/**
  * Author: @aguestuser
  * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
  */

trait Config {

  private val config = ConfigFactory.load()
  val env = Environment.of(sys.env("WHEREAT_ENVIRONMENT"))

  // db config
  val db = Environment.dbFor(env)

  // http configs
  private val httpConfig = config.getConfig("http")
  val httpInterface = httpConfig.getString("interface")
  val httpPort = Properties.envOrElse("PORT", httpConfig.getString("port")).toInt
  val allowedOrigin = config.getString("cors.allowed-origin")

  // hpkp configs
  val hpkpPinnedKey = sys.env("WHEREAT_PKP_PUBLIC_KEY")
  val hpkpBackupKey = sys.env("WHEREAT_PKP_BACKUP_KEY")
  val hpkpEmergencyKey = sys.env("WHEREAT_PKP_EMERGENCY_KEY")
  val hpkpReportURI = sys.env("WHEREAT_PKP_REPORT_URI")
  val hpkpMaxAge = sys.env("WHEREAT_PKP_MAX_AGE")
 }

