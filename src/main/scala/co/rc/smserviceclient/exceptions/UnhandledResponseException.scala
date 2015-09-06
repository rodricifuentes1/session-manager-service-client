package co.rc.smserviceclient.exceptions

import spray.http.HttpResponse

/**
 * Class that defines an UnhandledResponseException
 */
class UnhandledResponseException( response: HttpResponse ) extends SessionServiceClientException( s"The external service returned an unexpected response: $response" )
