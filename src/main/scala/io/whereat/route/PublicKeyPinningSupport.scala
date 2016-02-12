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

package io.whereat.route

import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.{Route, Directive0}
import akka.http.scaladsl.server.Directives._
import io.whereat.config.Config


trait PublicKeyPinningSupport extends Config {
  def pkpHandler(r:Route) = {
    publicKeyPinningHeader {
      r
    }
  }
  private def pkpHeader: RawHeader = {
    def assemblePins(ps:String*):String = {
      ps.foldLeft(""){
        (acc,pin) => acc + s"""pin-sha256="$pin"; """
      }
    }
   RawHeader(
     "Public-Key-Pins",
     assemblePins(hpkpPinnedKey,hpkpBackupKey,hpkpEmergencyKey) + s"""includeSubdomains; report-uri="$hpkpReportURI";max-age=$hpkpMaxAge"""
   )
  }
  private def publicKeyPinningHeader: Directive0 = {
    respondWithHeader(pkpHeader)
  }
}
