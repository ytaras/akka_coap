name := "akka_coap"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++=  Seq(
  "org.scodec" %% "scodec-bits" % "1.1.2",
  "org.scodec" %% "scodec-core" % "1.10.3",
  "org.eclipse.californium" % "californium-core" % "2.0.0-M2" % "test",
  "org.specs2" %% "specs2-core" % "3.8.5" % "test",
  "org.specs2" %% "specs2-scalacheck" % "3.8.5" % "test"
)

libraryDependencies ++= {
  if (scalaBinaryVersion.value startsWith "2.10")
    Seq(compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full))
  else Nil
}

resolvers += "uni-luebeck.de" at "https://maven.itm.uni-luebeck.de/content/repositories/releases"
