package co.rc.smserviceclient.exceptions

import spray.http.HttpResponse

/**
 * Class that defines an UnparseableResponseException
 */
class UnparseableResponseException( response: HttpResponse ) extends SessionServiceClientException( s"The external service returned an unparseable response: $response" )
