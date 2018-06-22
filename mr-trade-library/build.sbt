name := """mr-trade-library"""

version := "1.0"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats" % "0.9.0",
  "com.typesafe.slick" %% "slick" % "3.2.1",
  "com.typesafe.play" %% "play-slick" % "3.0.3",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.3",
  "com.typesafe.play" %% "play-json" % "2.6.8",
  "com.github.tminglei" %% "slick-pg" % "0.15.2",
  "com.typesafe.akka" %% "akka-remote" % "2.5.9",
  "com.typesafe.akka" %% "akka-actor" % "2.5.9"
)

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
