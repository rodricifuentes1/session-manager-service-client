package co.rc.smserviceclient.infrastructure.acl.dtos.responses

import argonaut._, Argonaut._

/**
 * Class that represents an error response
 * @param response Error response message
 * @param statusCode Error response status code
 */
case class ErrorResponseDTO( response: String,
  statusCode: Option[ Int ] = None ) extends HandledResponse

/**
 * Companion object for ErrorResponseDTO
 */
object ErrorResponseDTO {

  /**
   * Implicit marshaller for ErrorResponseDTO
   * @return ErrorResponseDTO CodecJson
   */
  implicit def ErrorResponseDTOCodec: CodecJson[ ErrorResponseDTO ] =
    casecodec2( ErrorResponseDTO.apply, ErrorResponseDTO.unapply )( "response", "statusCode" )
}