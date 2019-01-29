name := "KafkaStreams"

version := "0.1"

scalaVersion := "2.12.8"


lazy val akkaVersion = "2.5.19"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.apache.kafka" %% "kafka-streams-scala" % "2.1.0",
  "javax.ws.rs" % "javax.ws.rs-api" % "2.1.1" artifacts(Artifact("javax.ws.rs-api", "jar", "jar")),
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)