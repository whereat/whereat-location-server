package routes

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.server.{Route, Directive0}
import akka.http.scaladsl.server.Directives._
import cfg.Config

/**
 * Author: @aguestuser, borrowed from: https://groups.google.com/forum/#!topic/akka-user/5RCZIJt7jHo
 * License: GPLv2 (https://www.gnu.org/licenses/gpl-2.0.html)
 */

trait CorsSupport extends Config {

  lazy val allowed = { HttpOrigin(allowedOrigin) }

  val corsHandler = { r: Route ⇒
    accessControlHeaders {
      preflightRequestHandler ~ r
    }
  }

  private def accessControlHeaders: Directive0 = {
    mapResponseHeaders { headers ⇒
      `Access-Control-Allow-Origin`(allowed) +:
      `Access-Control-Allow-Credentials`(false) +:
      `Access-Control-Allow-Headers`(
        "Accept, Authorization", "Content-Type", "Origin", "X-Requested-With") +:
      headers
    }
  }

  private def preflightRequestHandler: Route = options {
    complete {
      HttpResponse(200).withHeaders {
        `Access-Control-Allow-Methods`( OPTIONS, POST, PUT, GET, DELETE )
      }
    }
  }
}
