package integration.support

import java.net.ServerSocket

import cfg.Config

trait TestConfig extends Config {
  override val httpPort = new ServerSocket(0).getLocalPort
}
