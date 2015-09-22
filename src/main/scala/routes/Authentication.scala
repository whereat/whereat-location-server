package routes

import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directive0
import akka.http.scaladsl.server.Directives._
import cfg.Config


trait PublicKeyPinningSupport extends Config {
//  val pinningHandler = { r:Route =>
//    publicKeyPinningHeader {
//
//    }
//  }
  private def pkpHeader: HttpHeader = {
   var hdr = RawHeader("Public-Key-Pins", s"""pin-sha256="vt+Sc+9c5geACQs4QweytEJL2ldM6XcLFuJ6hFO8ASE"; includeSubdomains;""")
  }
  private def publicKeyPinningHeader: Directive0 = {
    mapResponseHeaders { headers =>
      pkpHeader +: headers
    }
  }
}
