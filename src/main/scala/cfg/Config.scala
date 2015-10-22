package cfg

import com.typesafe.config.ConfigFactory
import scala.util.Properties
import slick.driver.H2Driver.api._


/**
  * Author: @aguestuser
  * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
  */

trait Config {
  private val config = ConfigFactory.load()
  private val httpConfig = config.getConfig("http")

  val httpInterface = httpConfig.getString("interface")
  val httpPort = Properties.envOrElse("PORT", httpConfig.getString("port")).toInt
  val db = Database.forConfig("devDb")
  val allowedOrigin = config.getString("cors.allowed-origin")
  val hpkpPinnedKey = sys.env("WHEREAT_PKP_PUBLIC_KEY")
  val hpkpBackupKey = sys.env("WHEREAT_PKP_BACKUP_KEY")
  val hpkpEmergencyKey = sys.env("WHEREAT_PKP_EMERGENCY_KEY")
  val hpkpReportURI = sys.env("WHEREAT_PKP_REPORT_URI")
  val hpkpMaxAge = sys.env("WHEREAT_PKP_MAX_AGE")
 }
