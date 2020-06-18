name := """play-scala-seed"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(resolvers ++= Seq(
    Resolver.bintrayRepo("hmrc", "releases"),
    Resolver.jcenterRepo
  ))

scalaVersion := "2.11.11"

libraryDependencies += guice
libraryDependencies += "uk.gov.hmrc"            %% "simple-reactivemongo" % "7.26.0-play-26"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play"   % "3.1.2"          % Test
libraryDependencies += "uk.gov.hmrc"            %% "hmrctest"             % "3.9.0-play-26"  % Test
libraryDependencies += "org.mockito"            %  "mockito-core"         % "2.28.2"         % Test

herokuAppName in Compile := "aqueous-plateau-01338"
herokuProcessTypes in Compile := Map(
  "web" -> "target/universal/stage/bin/play-scala-seed -Dhttp.port=$PORT -Dplay.http.secret.key=${APPLICATION_SECRET} -Dmongodb.uri=${MONGODB_URI}"
)