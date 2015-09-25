package routes

import akka.http.scaladsl.server.Route

trait Headers extends PublicKeyPinningSupport with CorsSupport {
 def headers(r:Route):Route = {
   pkpHandler { corsHandler(r) }
 }
}
