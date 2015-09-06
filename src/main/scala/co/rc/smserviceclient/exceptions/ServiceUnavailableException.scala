package co.rc.smserviceclient.exceptions

import spray.http.HttpResponse

/**
 * Class that defines a ServiceUnavailableException
 */
class ServiceUnavailableException( response: HttpResponse ) extends SessionServiceClientException( s"The external service is unavailable: $response" )
