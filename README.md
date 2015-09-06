## Session manager service client
Session manager service client is a lightweight http client on top of [spray client](http://spray.io/documentation/1.2.2/spray-client/) that consumes [Session Manager Rest Service](https://github.com/rodricifuentes1/session-manager-service) to provide session management for any application.
## Adding the dependency
* Modify your `build.sbt` file
```
  resolvers += Resolver.bintrayRepo("rodricifuentes1", "RC-releases")
  libraryDependencies += "co.rc" %% "session-manager-service-client" % "1.0"
```
* You can also download the compiled `jar file` here [ ![Download](https://api.bintray.com/packages/rodricifuentes1/RC-releases/session-manager-service-client/images/download.svg) ](https://bintray.com/rodricifuentes1/RC-releases/session-manager-service-client/_latestVersion)
## Usage
### Download the compiled version here [ ![Download](https://api.bintray.com/packages/rodricifuentes1/RC-releases/session-manager-client/images/download.svg) ](https://bintray.com/rodricifuentes1/RC-releases/session-manager-service-client/_latestVersion)

### Configuration
Session manager service HTTP Client has a configuration file called `reference.conf`, defined with these settings:
```
co.rc.smserviceclient {
  application-key = "app2-key" //Client Authentication Key
  service {
    request-timeout = 10 seconds // Maximum wait time for the request to complete
    url = "http://0.0.0.0:7777" // Url where to contact Session Manager REST Service
    base-path = "session_manager" // Constant to build request url. Must match the Session Manager REST Service base path published
    sessions-path = "sessions" // Constant to build request url. Must match the Session Manager REST Service sessions path published
  }
}
```
