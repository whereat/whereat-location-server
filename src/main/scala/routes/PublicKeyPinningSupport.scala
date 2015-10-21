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
    def assemblePins(ps:String*):String = {
      ps.foldLeft(""){
        (acc,pin) => acc + s"""pin-sha256="$pin"; """
      }
    }
   RawHeader(
     "Public-Key-Pins",
     assemblePins(hpkpPinnedKey,hpkpBackupKey) + s"""includeSubdomains; report-uri="$hpkpReportURI";max-age=$hpkpMaxAge"""
   )
  }
  private def publicKeyPinningHeader: Directive0 = {
    respondWithHeader(pkpHeader)
  }
}
