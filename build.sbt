name := """play-scala-seed"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.11"

libraryDependencies += guice
libraryDependencies += "uk.gov.hmrc" %% "simple-reactivemongo" % "5.2.0" exclude("org.reactivemongo", "reactivemongo")
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.3" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
