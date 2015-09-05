package co.rc.smserviceclient.infrastructure.acl.dtos.responses

/**
 * Class that represents a successful response
 * @param response Response description
 * @param sessionId session id. Optional.
 * @param sessionWasUpdated A boolean that indicates if session was updated. Optional.
 * @param sessionData Session data. Optional.
 */
case class SuccessfulResponseDTO( response: String,
  sessionId: Option[ String ] = None,
  sessionWasUpdated: Option[ Boolean ] = None,
  sessionData: Option[ String ] = None )
