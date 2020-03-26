
scalaVersion := "2.12.8"

name := "svfa-scala"
organization := "br.unb.cic"
version := "0.0.1"

libraryDependencies += "org.typelevel" %% "cats-core" % "1.6.0"

parallelExecution in Test := false

resolvers += "soot snapshots" at "https://soot-build.cs.uni-paderborn.de/nexus/repository/soot-snapshot/"

resolvers += "soot releases" at "https://soot-build.cs.uni-paderborn.de/nexus/repository/soot-release/"

resolvers += Classpaths.typesafeReleases

libraryDependencies += "ca.mcgill.sable" % "soot" % "3.3.0"
// libraryDependencies += "de.fraunhofer.iem" % "idealPDS" % "2.4-SNAPSHOT"
// libraryDependencies += "de.fraunhofer.iem" % "boomerangPDS" % "2.4-SNAPSHOT"
libraryDependencies += "com.google.guava" % "guava" % "27.1-jre"
libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.11.2"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.11.2"
libraryDependencies += "org.scala-graph" %% "graph-core" % "1.13.0"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))