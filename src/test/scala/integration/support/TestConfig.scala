package integration.support

import java.net.ServerSocket

import io.whereat.config.Config

trait TestConfig extends Config {
  override val httpPort = new ServerSocket(0).getLocalPort
}
