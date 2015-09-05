package co.rc.smserviceclient.infrastructure.acl.dtos.requests

/**
 * Class that represents a session dto
 * @param id Session id
 * @param data Session data to store - Optional
 * @param expirationTime Session expiration time - Optional
 */
case class SessionDTO( id: String,
  data: Option[ String ],
  expirationTime: Option[ ExpirationTimeDTO ] )
