package co.rc.smserviceclient.infrastructure.acl.dtos.responses

/**
 * Trait that defines a response that is correctly handled
 */
abstract class HandledResponse {

  /**
   * Response message
   */
  def response: String

  /**
   * Response status code
   */
  def statusCode: Option[ Int ]

}
