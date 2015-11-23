package actors

/**
 * Written with love for where@
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

trait ReadCacheWindow {
  val interval: Long
  val name: String
}
case object ReadCache5Sec extends ReadCacheWindow {
  val interval = 5000L
  val name = "5SecReadCache"
}
case object ReadCache30Sec extends ReadCacheWindow {
  val interval = 30000L
  val name = "30SecReadCache"
}
case object ReadCache1Min extends ReadCacheWindow {
  val interval = 60000L
  val name = "1MinReadCache"
}
