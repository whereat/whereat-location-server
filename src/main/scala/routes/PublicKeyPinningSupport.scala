package routes

import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.{Route, Directive0}
import akka.http.scaladsl.server.Directives._
import cfg.Config


trait PublicKeyPinningSupport extends Config {
  def pkpHandler(r:Route) = {
    publicKeyPinningHeader {
      r
    }
  }
  private def pkpHeader: RawHeader = {
   RawHeader(
     "Public-Key-Pins",
     s"""pin-sha256="$hpkpPinnedKey"; pin-sha256="$hpkpBackupKey"; includeSubdomains; report-uri="$hpkpReportURI";max-age=$hpkpMaxAge"""
   )
  }
  private def publicKeyPinningHeader: Directive0 = {
    respondWithHeader(pkpHeader)
  }
}
