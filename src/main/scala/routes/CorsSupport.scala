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

package routes

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.server.{Route, Directive0}
import akka.http.scaladsl.server.Directives._
import cfg.Config


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
      `Access-Control-Allow-Credentials`(true) +:
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
