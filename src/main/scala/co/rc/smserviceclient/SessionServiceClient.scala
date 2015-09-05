package co.rc.smserviceclient

import akka.actor.ActorSystem
import com.typesafe.config.Config

/**
 * Class that represents a HTTP service client for session manager
 * @param system Implicit actor system for http request
 * @param config Implicit application configuration
 */
class SessionServiceClient()( implicit system: ActorSystem, config: Config ) {

  /**
   * Execution context for future manipulation
   */
  import system.dispatcher

}
