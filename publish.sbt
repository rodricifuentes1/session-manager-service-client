publishMavenStyle := true

pomIncludeRepository := { _ => false }

publishArtifact in Test := false

bintrayRepository := "RC-releases"

bintrayPackageLabels := Seq("scala", "spray", "spray client", "session manager client", "session management client")