package co.rc.smserviceclient.infrastructure.acl.dtos.requests

import argonaut._, Argonaut._

/**
 * Class that represents a session dto
 * @param id Session id
 * @param data Session data to store - Optional
 * @param expirationTime Session expiration time - Optional
 */
case class SessionDTO( id: String,
  data: Option[ String ],
  expirationTime: Option[ ExpirationTimeDTO ] )

/**
 * Companion object for SessionDTO
 */
object SessionDTO {

  /**
   * Implicit marshaller for SessionDTO
   * @return SessionDTO CodecJson
   */
  implicit def SessionDTOCodec: CodecJson[ SessionDTO ] =
    casecodec3( SessionDTO.apply, SessionDTO.unapply )( "id", "data", "expirationTime" )
}
