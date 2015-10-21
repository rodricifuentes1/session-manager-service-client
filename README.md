## Session manager service client
Session manager service client is a lightweight http client on top of [spray client](http://spray.io/documentation/1.2.2/spray-client/) that consumes [Session Manager Rest Service](https://github.com/rodricifuentes1/session-manager-service) to provide session management for any application.
## Adding the dependency
* Modify your `build.sbt` file
```
  resolvers += Resolver.bintrayRepo("rodricifuentes1", "RC-releases")
  libraryDependencies += "co.rc" %% "session-manager-service-client" % "1.1"
```
* You can also download the compiled `jar file` here [ ![Download](https://api.bintray.com/packages/rodricifuentes1/RC-releases/session-manager-service-client/images/download.svg) ](https://bintray.com/rodricifuentes1/RC-releases/session-manager-service-client/_latestVersion)

## Usage
For further information please read [session manager rest service documentation](https://github.com/rodricifuentes1/session-manager-service/blob/master/README.md)
### Configuration
Session manager service HTTP Client has a `reference.conf` file defined with these settings:
```
co.rc.smserviceclient {
  application-key = "app2-key" // Client Authentication Key. This key is used to authenticate this client into the rest service and must exist in co.rc.smservice.security.allowed-keys service property
  service {
    request-timeout = 10 seconds // Maximum wait time for the request to complete
    url = "http://0.0.0.0:7777" // Url where to contact Session Manager REST Service
    base-path = "session_manager" // Constant to build request url. Must match the Session Manager REST Service base path published
    sessions-path = "sessions" // Constant to build request url. Must match the Session Manager REST Service sessions path published
  }
}
```
You should override this default settings in your `application.conf` file.
### Create an instance of `SessionServiceClient`
```scala
  import akka.actor.ActorSystem
  import com.typesafe.config.Config
  import com.typesafe.config.ConfigFactory
  ...
  
  implicit val system: ActorSystem = ActorSystem( "my-system" )
  
  // This will load config automatically using ConfigFactory.load()
  val client: SessionServiceClient = new SessionServiceClient()
  
  // You can also specify a custom config
  val config: Config = ConfigFactory.load("custom-config.conf")
  val client: SessionServiceClient = new SessionServiceClient( config )
```
### Create session
```scala

val sessionId: String = "session-1"
val sessionData: Option[String] = Some( "my-session-data" ) // Optional parameter
val sessionExpirationTime: ExpirationTimeDTO = ExpirationTime( 10, "minutes" ) // Optional parameter

val createFuture: Future[ Either[ SessionServiceClientException, HandledResponse ] ] = client.createSession( sessionId, sessionData, sessionExpirationTime )
createFuture.map {
  case Right( response ) => response match {
    case r: SuccessfulResponseDTO => // The external service returned a successful response --> StatusCodes 201 and 409
    case r: ErrorResponseDTO => // The external service returned an error response --> StatusCodes 400 and 401
  }
  case Left( ex ) => ex match {
    case e: UnhandledResponseException => // The external service returned a response that is not handled by the client
    case e: ServiceUnavailableException => // The external service is not available
    case e: UnparseableResponseException => // The external service returned a response that cannot be unmarshalled
    case _ => ...
  } 
}
```
### Query session
```scala

val sessionId: String = "session-1"

val queryFuture: Future[ Either[ SessionServiceClientException, HandledResponse ] ] = client.querySession( sessionId )
queryFuture.map {
  case Right( response ) => response match {
    case r: SuccessfulResponseDTO => // The external service returned a successful response --> StatusCodes 200 and 404
    case r: ErrorResponseDTO => // The external service returned an error response --> StatusCode 401
  }
  case Left( ex ) => ex match {
    case e: UnhandledResponseException => // The external service returned a response that is not handled by the client
    case e: ServiceUnavailableException => // The external service is not available
    case e: UnparseableResponseException => // The external service returned a response that cannot be unmarshalled
    case _ => ...
  } 
}
```
### Delete session
```scala

val sessionId: String = "session-1"

val deleteFuture: Future[ Either[ SessionServiceClientException, HandledResponse ] ] = client.querySession( sessionId )
deleteFuture.map {
  case Right( response ) => response match {
    case r: SuccessfulResponseDTO => // The external service returned a successful response --> StatusCodes 200 and 404
    case r: ErrorResponseDTO => // The external service returned an error response --> StatusCode 401
  }
  case Left( ex ) => ex match {
    case e: UnhandledResponseException => // The external service returned a response that is not handled by the client
    case e: ServiceUnavailableException => // The external service is not available
    case e: UnparseableResponseException => // The external service returned a response that cannot be unmarshalled
    case _ => ...
  } 
}
```
### Build this project
1. `clone` this project.
2. Execute `sbt` command in project directory.
3. Execute `update` and `compile` in sbt console.
4. To generate project for Intellij-Idea ide, execute `gen-idea` command in sbt console.
5. To run tests execute `test` command in sbt console.
6. To generate tests report using `scoverage` plugin, execute `coverage` and `test` commans in sbt console. This will generate tests report under `target/scala_2.11/scoverage_report` folder.

### Test code coverage - 100%
## Changelog
v1.1 (current)
* Removed implicit config parameter for SessionServiceClient class constructor

[v1.0](https://github.com/rodricifuentes1/session-manager-service-client/tree/v1.0) - First release
