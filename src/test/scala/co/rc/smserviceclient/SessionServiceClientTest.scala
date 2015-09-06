package co.rc.smserviceclient

import co.rc.smserviceclient.exceptions.{ UnhandledResponseException, ServiceUnavailableException, UnparseableResponseException, SessionServiceClientException }
import co.rc.smserviceclient.infrastructure.acl.dtos.responses.{ ErrorResponseDTO, SuccessfulResponseDTO, HandledResponse }

import org.specs2.mutable.Specification

import utils.SmServiceClientTestContext

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._

import spray.http._
import spray.http.HttpResponse

/**
 * Class that implements tests for SessionServiceClient
 */
class SessionServiceClientTest extends Specification {
  sequential

  "SessionServiceClient" should {

    // ----------------------------
    // Create tests
    // ----------------------------

    "CREATE: Request a new session creation with successful response (201)" in new SmServiceClientTestContext {

      // Successful response
      val jsonResponse: String =
        """
          |{
          |    "response": "Session was created",
          |    "sessionId": "1"
          |}
        """.stripMargin
      val successfulResponse = HttpResponse( StatusCodes.Created, HttpEntity( ContentTypes.`application/json`, jsonResponse ) )

      // Client instance
      val client: SessionServiceClient = new SessionServiceClient() {
        override def request = ( req: HttpRequest ) => Future( successfulResponse )
      }

      // Request
      val creationFuture: Future[ Either[ SessionServiceClientException, HandledResponse ] ] = client.createSession( "1", Some( "hello world" ) )
      val creationResult: Either[ SessionServiceClientException, HandledResponse ] = Await.result( creationFuture, 10.seconds )

      // Assertions
      creationResult.isRight must_== true
      creationResult.right.get must beAnInstanceOf[ SuccessfulResponseDTO ]
      creationResult.right.get.asInstanceOf[ SuccessfulResponseDTO ].response must_== "Session was created"
      creationResult.right.get.asInstanceOf[ SuccessfulResponseDTO ].sessionId must_== Some( "1" )

    }

    "CREATE: Request a new session creation with successful response with conflicts (409)" in new SmServiceClientTestContext {

      // Successful response
      val jsonResponse: String =
        """
          |{
          |    "response": "Session already exist",
          |    "sessionId": "1",
          |    "sessionWasUpdated": false,
          |    "sessionData": "{ \"myIntData\": 10, \"myStringData\" : \"hello world\" }"
          |}
        """.stripMargin
      val successfulResponse = HttpResponse( StatusCodes.Conflict, HttpEntity( ContentTypes.`application/json`, jsonResponse ) )

      // Client instance
      val client: SessionServiceClient = new SessionServiceClient() {
        override def request = ( req: HttpRequest ) => Future( successfulResponse )
      }

      // Request
      val creationFuture: Future[ Either[ SessionServiceClientException, HandledResponse ] ] = client.createSession( "1", Some( "hello world" ) )
      val creationResult: Either[ SessionServiceClientException, HandledResponse ] = Await.result( creationFuture, 10.seconds )

      // Assertions
      creationResult.isRight must_== true
      creationResult.right.get must beAnInstanceOf[ SuccessfulResponseDTO ]
      creationResult.right.get.asInstanceOf[ SuccessfulResponseDTO ].response must_== "Session already exist"
      creationResult.right.get.asInstanceOf[ SuccessfulResponseDTO ].sessionId must_== Some( "1" )
      creationResult.right.get.asInstanceOf[ SuccessfulResponseDTO ].sessionWasUpdated must_== Some( false )
      creationResult.right.get.asInstanceOf[ SuccessfulResponseDTO ].sessionData must_== Some( "{ \"myIntData\": 10, \"myStringData\" : \"hello world\" }" )

    }

    "CREATE: Request a new session creation with error response (400)" in new SmServiceClientTestContext {

      // Error response
      val stringResponse: String = "The request content was malformed:\nrequirement failed: Session id must not be empty"
      val errorResponse = HttpResponse( StatusCodes.BadRequest, HttpEntity( ContentTypes.`text/plain`, stringResponse ) )

      // Client instance
      val client: SessionServiceClient = new SessionServiceClient() {
        override def request = ( req: HttpRequest ) => Future( errorResponse )
      }

      // Request
      val creationFuture: Future[ Either[ SessionServiceClientException, HandledResponse ] ] = client.createSession( "1", Some( "hello world" ) )
      val creationResult: Either[ SessionServiceClientException, HandledResponse ] = Await.result( creationFuture, 10.seconds )

      // Assertions
      creationResult.isRight must_== true
      creationResult.right.get must beAnInstanceOf[ ErrorResponseDTO ]
      creationResult.right.get.asInstanceOf[ ErrorResponseDTO ].response must_== "The request content was malformed:\nrequirement failed: Session id must not be empty"
      creationResult.right.get.asInstanceOf[ ErrorResponseDTO ].statusCode must_== Some( 400 )

    }

    "CREATE: Request a new session creation with error response (401)" in new SmServiceClientTestContext {

      // Error response
      val stringResponse: String = "The supplied authentication is invalid"
      val errorResponse = HttpResponse( StatusCodes.Unauthorized, HttpEntity( ContentTypes.`text/plain`, stringResponse ) )

      // Client instance
      val client: SessionServiceClient = new SessionServiceClient() {
        override def request = ( req: HttpRequest ) => Future( errorResponse )
      }

      // Request
      val creationFuture: Future[ Either[ SessionServiceClientException, HandledResponse ] ] = client.createSession( "1", Some( "hello world" ) )
      val creationResult: Either[ SessionServiceClientException, HandledResponse ] = Await.result( creationFuture, 10.seconds )

      // Assertions
      creationResult.isRight must_== true
      creationResult.right.get must beAnInstanceOf[ ErrorResponseDTO ]
      creationResult.right.get.asInstanceOf[ ErrorResponseDTO ].response must_== "The supplied authentication is invalid"
      creationResult.right.get.asInstanceOf[ ErrorResponseDTO ].statusCode must_== Some( 401 )

    }

    // ----------------------------
    // Query tests
    // ----------------------------

    "QUERY: Request a session query with successful response (200)" in new SmServiceClientTestContext {

      // Successful response
      val jsonResponse: String =
        """
          |{
          |    "response": "Session was found",
          |    "sessionId": "1",
          |    "sessionWasUpdated": true,
          |    "sessionData": "{ \"myIntData\": 10, \"myStringData\" : \"hello world\" }"
          |}
        """.stripMargin
      val successfulResponse = HttpResponse( StatusCodes.OK, HttpEntity( ContentTypes.`application/json`, jsonResponse ) )

      // Client instance
      val client: SessionServiceClient = new SessionServiceClient() {
        override def request = ( req: HttpRequest ) => Future( successfulResponse )
      }

      // Request
      val queryFuture: Future[ Either[ SessionServiceClientException, HandledResponse ] ] = client.querySession( "1" )
      val queryResult: Either[ SessionServiceClientException, HandledResponse ] = Await.result( queryFuture, 10.seconds )

      // Assertions
      queryResult.isRight must_== true
      queryResult.right.get must beAnInstanceOf[ SuccessfulResponseDTO ]
      queryResult.right.get.asInstanceOf[ SuccessfulResponseDTO ].response must_== "Session was found"
      queryResult.right.get.asInstanceOf[ SuccessfulResponseDTO ].statusCode must_== Some( 200 )
      queryResult.right.get.asInstanceOf[ SuccessfulResponseDTO ].sessionId must_== Some( "1" )
      queryResult.right.get.asInstanceOf[ SuccessfulResponseDTO ].sessionWasUpdated must_== Some( true )
      queryResult.right.get.asInstanceOf[ SuccessfulResponseDTO ].sessionData must_== Some( "{ \"myIntData\": 10, \"myStringData\" : \"hello world\" }" )

    }

    "QUERY: Request a session query with successful response with not found status (404)" in new SmServiceClientTestContext {

      // Successful response
      val jsonResponse: String =
        """
          |{
          |    "response": "Requested session was not found or does not exist"
          |}
        """.stripMargin
      val successfulResponse = HttpResponse( StatusCodes.NotFound, HttpEntity( ContentTypes.`application/json`, jsonResponse ) )

      // Client instance
      val client: SessionServiceClient = new SessionServiceClient() {
        override def request = ( req: HttpRequest ) => Future( successfulResponse )
      }

      // Request
      val queryFuture: Future[ Either[ SessionServiceClientException, HandledResponse ] ] = client.querySession( "1" )
      val queryResult: Either[ SessionServiceClientException, HandledResponse ] = Await.result( queryFuture, 10.seconds )

      // Assertions
      queryResult.isRight must_== true
      queryResult.right.get must beAnInstanceOf[ SuccessfulResponseDTO ]
      queryResult.right.get.asInstanceOf[ SuccessfulResponseDTO ].response must_== "Requested session was not found or does not exist"
      queryResult.right.get.asInstanceOf[ SuccessfulResponseDTO ].statusCode must_== Some( 404 )

    }

    "QUERY: Request a session query with error response (401)" in new SmServiceClientTestContext {

      // Error response
      val stringResponse: String = "The supplied authentication is invalid"
      val errorResponse = HttpResponse( StatusCodes.Unauthorized, HttpEntity( ContentTypes.`text/plain`, stringResponse ) )

      // Client instance
      val client: SessionServiceClient = new SessionServiceClient() {
        override def request = ( req: HttpRequest ) => Future( errorResponse )
      }

      // Request
      val queryFuture: Future[ Either[ SessionServiceClientException, HandledResponse ] ] = client.querySession( "1" )
      val queryResult: Either[ SessionServiceClientException, HandledResponse ] = Await.result( queryFuture, 10.seconds )

      // Assertions
      queryResult.isRight must_== true
      queryResult.right.get must beAnInstanceOf[ ErrorResponseDTO ]
      queryResult.right.get.asInstanceOf[ ErrorResponseDTO ].response must_== "The supplied authentication is invalid"
      queryResult.right.get.asInstanceOf[ ErrorResponseDTO ].statusCode must_== Some( 401 )

    }

    // ----------------------------
    // Delete tests
    // ----------------------------

    "DELETE: Request a session deletion with successful response (200)" in new SmServiceClientTestContext {

      // Successful response
      val jsonResponse: String =
        """
          |{
          |    "response": "Request executed successfully"
          |}
        """.stripMargin
      val successfulResponse = HttpResponse( StatusCodes.OK, HttpEntity( ContentTypes.`application/json`, jsonResponse ) )

      // Client instance
      val client: SessionServiceClient = new SessionServiceClient() {
        override def request = ( req: HttpRequest ) => Future( successfulResponse )
      }

      // Request
      val deleteFuture: Future[ Either[ SessionServiceClientException, HandledResponse ] ] = client.deleteSession( "1" )
      val deleteResult: Either[ SessionServiceClientException, HandledResponse ] = Await.result( deleteFuture, 10.seconds )

      // Assertions
      deleteResult.isRight must_== true
      deleteResult.right.get must beAnInstanceOf[ SuccessfulResponseDTO ]
      deleteResult.right.get.asInstanceOf[ SuccessfulResponseDTO ].response must_== "Request executed successfully"
      deleteResult.right.get.asInstanceOf[ SuccessfulResponseDTO ].statusCode must_== Some( 200 )

    }

    "DELETE: Request a session deletion with successful response and not found status (404)" in new SmServiceClientTestContext {

      // Successful response
      val jsonResponse: String =
        """
          |{
          |    "response": "Requested session was not found or does not exist"
          |}
        """.stripMargin
      val successfulResponse = HttpResponse( StatusCodes.NotFound, HttpEntity( ContentTypes.`application/json`, jsonResponse ) )

      // Client instance
      val client: SessionServiceClient = new SessionServiceClient() {
        override def request = ( req: HttpRequest ) => Future( successfulResponse )
      }

      // Request
      val deleteFuture: Future[ Either[ SessionServiceClientException, HandledResponse ] ] = client.deleteSession( "1" )
      val deleteResult: Either[ SessionServiceClientException, HandledResponse ] = Await.result( deleteFuture, 10.seconds )

      // Assertions
      deleteResult.isRight must_== true
      deleteResult.right.get must beAnInstanceOf[ SuccessfulResponseDTO ]
      deleteResult.right.get.asInstanceOf[ SuccessfulResponseDTO ].response must_== "Requested session was not found or does not exist"
      deleteResult.right.get.asInstanceOf[ SuccessfulResponseDTO ].statusCode must_== Some( 404 )

    }

    "DELETE: Request a session deletion with error response (401)" in new SmServiceClientTestContext {

      // Error response
      val stringResponse: String = "The supplied authentication is invalid"
      val errorResponse = HttpResponse( StatusCodes.Unauthorized, HttpEntity( ContentTypes.`text/plain`, stringResponse ) )

      // Client instance
      val client: SessionServiceClient = new SessionServiceClient() {
        override def request = ( req: HttpRequest ) => Future( errorResponse )
      }

      // Request
      val deleteFuture: Future[ Either[ SessionServiceClientException, HandledResponse ] ] = client.deleteSession( "1" )
      val deleteResult: Either[ SessionServiceClientException, HandledResponse ] = Await.result( deleteFuture, 10.seconds )

      // Assertions
      deleteResult.isRight must_== true
      deleteResult.right.get must beAnInstanceOf[ ErrorResponseDTO ]
      deleteResult.right.get.asInstanceOf[ ErrorResponseDTO ].response must_== "The supplied authentication is invalid"
      deleteResult.right.get.asInstanceOf[ ErrorResponseDTO ].statusCode must_== Some( 401 )

    }

    // ----------------------------
    // Exceptions tests
    // ----------------------------

    "EXCEPTION: Get an exception when successful response cannot be unmarshalled" in new SmServiceClientTestContext {

      // Successful response
      val jsonResponse: String =
        """
          |{
          |    "invalid1": 1,
          |    "invalid2: "invalid 2"
          |}
        """.stripMargin
      val successfulResponse = HttpResponse( StatusCodes.OK, HttpEntity( ContentTypes.`application/json`, jsonResponse ) )

      // Client instance
      val client: SessionServiceClient = new SessionServiceClient() {
        override def request = ( req: HttpRequest ) => Future( successfulResponse )
      }

      // Request
      val queryFuture: Future[ Either[ SessionServiceClientException, HandledResponse ] ] = client.querySession( "1" )
      val queryResult: Either[ SessionServiceClientException, HandledResponse ] = Await.result( queryFuture, 10.seconds )

      // Assertions
      queryResult.isLeft must_== true
      queryResult.left.get must beAnInstanceOf[ UnparseableResponseException ]

    }

    "EXCEPTION: Get an exception when external service is unavailable" in new SmServiceClientTestContext {

      // Successful response
      val stringResponse: String = "The server is currently unavailable (because it is overloaded or down for maintenance)"
      val errorResponse = HttpResponse( StatusCodes.ServiceUnavailable, HttpEntity( ContentTypes.`application/json`, stringResponse ) )

      // Client instance
      val client: SessionServiceClient = new SessionServiceClient() {
        override def request = ( req: HttpRequest ) => Future( errorResponse )
      }

      // Request
      val queryFuture: Future[ Either[ SessionServiceClientException, HandledResponse ] ] = client.querySession( "1" )
      val queryResult: Either[ SessionServiceClientException, HandledResponse ] = Await.result( queryFuture, 10.seconds )

      // Assertions
      queryResult.isLeft must_== true
      queryResult.left.get must beAnInstanceOf[ ServiceUnavailableException ]

    }

    "EXCEPTION: Get an exception when external service returns an unhandled response" in new SmServiceClientTestContext {

      // Successful response
      val stringResponse: String = "Unhandled response"
      val errorResponse = HttpResponse( StatusCodes.NotAcceptable, HttpEntity( ContentTypes.`application/json`, stringResponse ) )

      // Client instance
      val client: SessionServiceClient = new SessionServiceClient() {
        override def request = ( req: HttpRequest ) => Future( errorResponse )
      }

      // Request
      val queryFuture: Future[ Either[ SessionServiceClientException, HandledResponse ] ] = client.querySession( "1" )
      val queryResult: Either[ SessionServiceClientException, HandledResponse ] = Await.result( queryFuture, 10.seconds )

      // Assertions
      queryResult.isLeft must_== true
      queryResult.left.get must beAnInstanceOf[ UnhandledResponseException ]

    }

  }

}
