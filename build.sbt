name := "akka_coap"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++=  {
  val californiumVersion = "2.0.0-M2"
  Seq(
    "org.eclipse.californium" % "californium-core" % californiumVersion,
    // TODO - Only http core will be depended on
    "com.typesafe.akka" %% "akka-http" % "10.0.0"
  )
}

