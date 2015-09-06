package co.rc.smserviceclient

import akka.actor.ActorSystem
import akka.util.Timeout

import argonaut._, Argonaut._

import co.rc.smserviceclient.exceptions.{ ServiceUnavailableException, SessionServiceClientException, UnparseableResponseException, UnhandledResponseException }
import co.rc.smserviceclient.infrastructure.acl.dtos.requests.{ SessionDTO, ExpirationTimeDTO }
import co.rc.smserviceclient.infrastructure.acl.dtos.responses.{ HandledResponse, ErrorResponseDTO, SuccessfulResponseDTO }

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging

import net.ceedubs.ficus.Ficus._

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.Future

import spray.http._
import spray.client.pipelining._
import spray.http.HttpRequest
import spray.http.HttpResponse

/**
 * Class that represents a HTTP service client for session manager
 * @param system Implicit actor system for http request
 * @param config Implicit application configuration
 */
class SessionServiceClient()( implicit system: ActorSystem, config: Config ) extends LazyLogging {

  /**
   * Execution context for future manipulation
   */
  import system.dispatcher

  /**
   * Pipeline builder for http requests
   */
  // $COVERAGE-OFF$
  def request: HttpRequest => Future[ HttpResponse ] = {
    val appKey: String = config.as[ String ]( "co.rc.smserviceclient.application-key" )
    addHeader( "app-key", appKey ) ~> sendReceive
  }
  // $COVERAGE-ON$

  /**
   * Method that sends
   * @param sessionId Session id
   * @param sessionData Session data
   * @param sessionExpirationTime Session expiration time
   * @return A future with an Either inside
   *         Left with this SessionServiceClientException exceptions:
   *         1. ServiceUnavailableException when service is unavailable
   *         2. UnhandledResponseException when service response is unhandled
   *         3. UnparseableResponseException when service response can not be unmarshalled
   *         Right with HandledResponse class:
   *         1. SuccessfulResponseDTO when service response is successful
   *         2. ErrorResponseDTO when service response is not successful
   */
  def createSession( sessionId: String,
    sessionData: Option[ String ] = None,
    sessionExpirationTime: Option[ ExpirationTimeDTO ] = None ): Future[ Either[ SessionServiceClientException, HandledResponse ] ] = {
    // Configured timeout for request
    implicit val requestTimeout: Timeout = config.as[ FiniteDuration ]( "co.rc.smserviceclient.service.request-timeout" )
    // Create entity with supplied data and convert it to json object
    val entity: SessionDTO = SessionDTO( sessionId, sessionData, sessionExpirationTime )
    val jsonEntity: String = entity.asJson.nospaces
    // Spray client http request && http response handling
    val response: Future[ HttpResponse ] = request( Post(
      getRequestUrl(),
      HttpEntity( ContentTypes.`application/json`, jsonEntity )
    ) )
    handleServiceResponse(
      response,
      List( StatusCodes.Created, StatusCodes.Conflict ),
      List( StatusCodes.BadRequest, StatusCodes.Unauthorized )
    )
  }

  /**
   * Method that queries a session
   * @param sessionId Session id to query
   * @return A future with an Either inside
   *         Left with this SessionServiceClientException exceptions:
   *         1. ServiceUnavailableException when service is unavailable
   *         2. UnhandledResponseException when service response is unhandled
   *         3. UnparseableResponseException when service response can not be unmarshalled
   *         Right with HandledResponse class:
   *         1. SuccessfulResponseDTO when service response is successful
   *         2. ErrorResponseDTO when service response is not successful
   */
  def querySession( sessionId: String ): Future[ Either[ SessionServiceClientException, HandledResponse ] ] = {
    // Configured timeout for request
    implicit val requestTimeout: Timeout = config.as[ FiniteDuration ]( "co.rc.smserviceclient.service.request-timeout" )
    // Spray client http request && http response handling
    val response: Future[ HttpResponse ] = request( Get( getRequestUrl( Some( sessionId ) ) ) )
    handleServiceResponse(
      response,
      List( StatusCodes.OK, StatusCodes.NotFound ),
      List( StatusCodes.Unauthorized )
    )
  }

  /**
   * Method that deletes a session
   * @param sessionId Session id to delete
   * @return A future with an Either inside
   *         Left with this SessionServiceClientException exceptions:
   *         1. ServiceUnavailableException when service is unavailable
   *         2. UnhandledResponseException when service response is unhandled
   *         3. UnparseableResponseException when service response can not be unmarshalled
   *         Right with HandledResponse class:
   *         1. SuccessfulResponseDTO when service response is successful
   *         2. ErrorResponseDTO when service response is not successful
   */
  def deleteSession( sessionId: String ): Future[ Either[ SessionServiceClientException, HandledResponse ] ] = {
    // Configured timeout for request
    implicit val requestTimeout: Timeout = config.as[ FiniteDuration ]( "co.rc.smserviceclient.service.request-timeout" )
    // Spray client http request && http response handling
    val response: Future[ HttpResponse ] = request( Delete( getRequestUrl( Some( sessionId ) ) ) )
    handleServiceResponse(
      response,
      List( StatusCodes.OK, StatusCodes.NotFound ),
      List( StatusCodes.Unauthorized )
    )
  }

  /**
   * Method that build request url
   * @param sessionId Session id
   * @param config implicit application configuration
   * @return Built url
   */
  private def getRequestUrl( sessionId: Option[ String ] = None )( implicit config: Config ): String = {
    // Session manager service url (ej. http://localhost:7777)
    val endpointUrl: String = config.as[ String ]( "co.rc.smserviceclient.service.url" )
    // Base path for session manager service (ej. default is 'session_manager')
    val basePath: String = config.as[ String ]( "co.rc.smserviceclient.service.base-path" )
    // Sessions path for session manager service (ej. default is 'sessions')
    val sessionsPath: String = config.as[ String ]( "co.rc.smserviceclient.service.sessions-path" )
    // Url builder: If sessionId is defined => endpoint/basePath/sessionsPath/{sessionId}
    // endpoint/basePath/sessionsPath otherwise
    sessionId match {
      case None       => s"$endpointUrl/$basePath/$sessionsPath"
      case Some( id ) => s"$endpointUrl/$basePath/$sessionsPath/$id"
    }
  }

  /**
   * Method that implements service response strategy.
   * @param futureResponse Http response future to map
   * @param successStatuses Expected http status codes that indicates a successful response
   * @param errorStatuses Expected http status codes that indicates an error response
   * @param unavailableStatuses Expected http status codes that indicates when session manager service is unavailable
   * @return A future with an Either inside
   *         Left with this SessionServiceClientException exceptions:
   *         1. ServiceUnavailableException when service is unavailable
   *         2. UnhandledResponseException when service response is unhandled
   *         3. UnparseableResponseException when service response can not be unmarshalled
   *         Right with HandledResponse class:
   *         1. SuccessfulResponseDTO when service response is successful
   *         2. ErrorResponseDTO when service response is not successful
   */
  private def handleServiceResponse( futureResponse: Future[ HttpResponse ],
    successStatuses: List[ StatusCode ],
    errorStatuses: List[ StatusCode ],
    unavailableStatuses: List[ StatusCode ] = List( StatusCodes.BadGateway, StatusCodes.ServiceUnavailable ) ): Future[ Either[ SessionServiceClientException, HandledResponse ] ] = futureResponse.map { response =>
    val ( isSuccess: Boolean, isError: Boolean, isUnavailable: Boolean ) = (
      successStatuses.contains( response.status ),
      errorStatuses.contains( response.status ),
      unavailableStatuses.contains( response.status )
    )
    isSuccess || isError match {
      case true if isSuccess => Parse.decodeOption[ SuccessfulResponseDTO ]( response.entity.asString )
        .map( rd => Right( rd.copy( statusCode = Some( response.status.intValue ) ) ) )
        .getOrElse( Left( new UnparseableResponseException( response ) ) )
      case true if isError        => Right( ErrorResponseDTO( response.entity.asString, Some( response.status.intValue ) ) )
      case false if isUnavailable => Left( new ServiceUnavailableException( response ) )
      case false                  => Left( new UnhandledResponseException( response ) )
    }
  }
}
