package cfg

import com.typesafe.config.ConfigFactory
import scala.util.Properties

/**
  * Author: @aguestuser
  * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
  */

trait Config {
   private val config = ConfigFactory.load()
   private val httpConfig = config.getConfig("http")

   val httpInterface = httpConfig.getString("interface")
   val httpPort = Properties.envOrElse("PORT", httpConfig.getString("port")).toInt
 }
