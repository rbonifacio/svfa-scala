scalaVersion := "2.12.20"

name := "svfa-scala"
organization := "br.unb.cic"

version := "0.3.0-SNAPSHOT"

githubOwner := "rbonifacio"
githubRepository := "svfa-scala"
githubTokenSource := TokenSource.GitConfig("github.token")

publishConfiguration := publishConfiguration.value.withOverwrite(true)
publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)

parallelExecution in Test := false


resolvers += "Local maven repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

resolvers += Classpaths.typesafeReleases

libraryDependencies += "org.typelevel" %% "cats-core" % "1.6.0"

libraryDependencies += "org.soot-oss" % "soot" % "4.5.0"
libraryDependencies += "de.fraunhofer.sit.sse.flowdroid" % "soot-infoflow" % "2.13.0"
libraryDependencies += "de.fraunhofer.sit.sse.flowdroid" % "soot-infoflow-cmd" % "2.13.0"
libraryDependencies += "de.fraunhofer.sit.sse.flowdroid" % "soot-infoflow-android" % "2.13.0"
libraryDependencies += "com.google.guava" % "guava" % "27.1-jre"
libraryDependencies += "org.scala-graph" %% "graph-core" % "1.13.0"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"

libraryDependencies += "javax.servlet" % "javax.servlet-api" % "3.0.1" % "provided"

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"

