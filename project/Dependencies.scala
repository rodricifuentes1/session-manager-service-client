import sbt._

object Dependencies {

  /**
  * Defines repository resolvers
  */
  val resolvers = Seq(
    "Scalaz releases" at "http://dl.bintray.com/scalaz/releases",
    "Sonatype releases" at "http://oss.sonatype.org/content/repositories/releases",
    "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
    "Typesafe releases" at "http://repo.typesafe.com/typesafe/releases/"
  )

  // -----------------------------------
  // VERSIONS
  // -----------------------------------
  
  // Spray && Akka
  val sprayVersion: String = "1.3.3"
  val akkaVersion: String = "2.3.13"

  // Logging
  val logbackVersion: String = "1.1.3"
  val scalaloggingVersion: String = "3.1.0"

  // Utils
  val ficusVersion: String = "1.1.2"
  val nScalaTimeVersion: String = "2.2.0"
  val argonautVersion: String = "6.1"
  
  // Testing
  val specs2Version: String = "3.6.4"
  

  // -----------------------------------
  // DEPENDENCIES
  // -----------------------------------
  val all = Seq(
    "io.spray" %%  "spray-client" % sprayVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,

    "ch.qos.logback" % "logback-classic" % logbackVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaloggingVersion,
    "io.argonaut" %% "argonaut" % argonautVersion,

    "net.ceedubs" %% "ficus" % ficusVersion,
    "com.github.nscala-time" %% "nscala-time" % nScalaTimeVersion,

    "org.specs2" %% "specs2-core" % specs2Version % "test"
  )

}
