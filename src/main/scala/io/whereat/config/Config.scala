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

import com.typesafe.config.ConfigFactory
import slick.driver.PostgresDriver.api.{Database ⇒ PGDatabase}

import scala.util.Properties


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

