package cfg

/**
 * Author: @aguestuser
 * Date: 10/28/15
 * License: GPLv2 (https://www.gnu.org/licenses/gpl-2.0.html)
 */

trait Environment
case object Prod extends Environment
case object Dev extends Environment
case object Local extends Environment

object Environment {

  def of(sVar: String): Environment = sVar match {
    case "DEVELOPMENT" ⇒ Dev
    case "LOCAL" ⇒ Local
    case _ ⇒ Prod
  }

}