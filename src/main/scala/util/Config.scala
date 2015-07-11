package util

import com.typesafe.config.ConfigFactory

import scala.util.Properties

/**
  * Author: @aguestuser
  * Date: 7/10/15
  * License: GPLv2 (https://www.gnu.org/licenses/gpl-2.0.html)
  */

trait Config {
   private val config = ConfigFactory.load()
   private val httpConfig = config.getConfig("http")

   val httpInterface = httpConfig.getString("interface")
   val httpPort = Properties.envOrElse("PORT", httpConfig.getString("port")).toInt
 }
