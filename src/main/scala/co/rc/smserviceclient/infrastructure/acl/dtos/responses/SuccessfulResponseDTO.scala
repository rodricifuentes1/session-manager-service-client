package co.rc.smserviceclient.infrastructure.acl.dtos.responses

import argonaut._, Argonaut._

/**
 * Class that represents a successful response
 * @param response Response description
 * @param statusCode Response status code
 * @param sessionId session id. Optional.
 * @param sessionWasUpdated A boolean that indicates if session was updated. Optional.
 * @param sessionData Session data. Optional.
 */
case class SuccessfulResponseDTO( response: String,
  statusCode: Option[ Int ] = None,
  sessionId: Option[ String ] = None,
  sessionWasUpdated: Option[ Boolean ] = None,
  sessionData: Option[ String ] = None ) extends HandledResponse

/**
 * Companion object for SuccessfulResponseDTO
 */
object SuccessfulResponseDTO {

  /**
   * Implicit marshaller for SuccessfulResponseDTO
   * @return SuccessfulResponseDTO CodecJson
   */
  implicit def SuccessfulResponseDTOCodec: CodecJson[ SuccessfulResponseDTO ] =
    casecodec5( SuccessfulResponseDTO.apply, SuccessfulResponseDTO.unapply )( "response", "statusCode", "sessionId", "sessionWasUpdated", "sessionData" )
}