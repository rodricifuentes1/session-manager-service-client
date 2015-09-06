package co.rc.smserviceclient.infrastructure.acl.dtos.requests

import argonaut._, Argonaut._

/**
 * Class that represents a expiration time dto
 * @param value Expiration time value
 * @param unit Expiration time unit
 */
case class ExpirationTimeDTO( value: Int, unit: String )

/**
 * Companion object for ExpirationTimeDTO
 */
object ExpirationTimeDTO {

  /**
   * Implicit marshaller for ExpirationTimeDTO
   * @return ExpirationTimeDTO CodecJson
   */
  implicit def ExpirationTimeDTOCodec: CodecJson[ ExpirationTimeDTO ] =
    casecodec2( ExpirationTimeDTO.apply, ExpirationTimeDTO.unapply )( "value", "unit" )
}
