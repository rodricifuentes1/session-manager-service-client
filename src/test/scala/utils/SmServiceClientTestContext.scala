package utils

import akka.actor.ActorSystem
import com.typesafe.config.{ ConfigFactory, Config }
import org.specs2.specification.After
import scala.concurrent.ExecutionContext

/**
 * Class that provides context for session manager service client tests
 */
abstract class SmServiceClientTestContext extends After {

  /**
   * Actor system for testing
   */
  implicit val system: ActorSystem = ActorSystem( "test-actor-system" )

  /**
   * Execution context
   */
  implicit val exc: ExecutionContext = system.dispatcher

  /**
   * App configuration
   */
  implicit val config: Config = ConfigFactory.load()

  /**
   * shutdown test actor system
   */
  override def after = system.shutdown()

}
