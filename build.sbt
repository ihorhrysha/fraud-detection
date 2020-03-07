name := "final-project"

ThisBuild / version := "0.1"

ThisBuild / scalaVersion := "2.12.8"

// These options will be used for *all* versions.
ThisBuild / scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-encoding", "UTF-8",
  "-Xlint")

resolvers in ThisBuild += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
resolvers in ThisBuild += "io.confluent" at "http://packages.confluent.io/maven/"


val akkaVersion = "2.5.20"

val commonDependencies = Seq(
  "org.apache.kafka" %% "kafka" % "2.1.0" withSources()
    exclude("org.slf4j","slf4j-log4j12")
    exclude("javax.jms", "jms")
    exclude("com.sun.jdmk", "jmxtools")
    exclude("com.sun.jmx", "jmxri"),
  "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  "org.slf4j" % "slf4j-log4j12" % "1.7.25",
  "junit" % "junit" % "4.12" % Test
)

val streamsDependencies = Seq(
  "org.apache.kafka" %% "kafka-streams-scala" % "2.0.1" withSources(),
  "org.apache.avro" % "avro" % "1.8.2",
  "io.confluent" % "kafka-streams-avro-serde" % "4.1.0",
  "io.confluent" % "kafka-avro-serializer" % "3.2.1",
  "javax.ws.rs" % "javax.ws.rs-api" % "2.1.1" artifacts Artifact("javax.ws.rs-api", "jar", "jar"),
  "org.apache.kafka" % "kafka-streams-test-utils" % "2.0.1" % Test
)

val akkaDependencies = Seq (
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
)

def dockerSettings(debugPort: Option[Int] = None) = Seq(
  dockerfile in docker := {
    val artifactSource: File = assembly.value
    val artifactTargetPath = s"/project/${artifactSource.name}"
    val scriptSourceDir = baseDirectory.value / "../scripts"
    val projectDir = "/project/"
    //Programming Dockerfile
    new Dockerfile {
      from("anapsix/alpine-java:latest")
      add(artifactSource, artifactTargetPath)
      copy(scriptSourceDir, projectDir)
      entryPoint(s"/project/start.sh")
      cmd(projectDir, s"${name.value}", s"${version.value}")
    }
  },
  imageNames in docker := Seq(
    ImageName(

      // TODO store locally
      registry = Some(sys.env("REGISTRY_URI")),
      namespace = Some("ucu-class"),
      repository = name.value,
      tag = Some(s"${sys.env("STUDENT_NAME")}-${version.value}")
    )
//    , ImageName(s"rickerlyman/${name.value}:latest")
  )
)

envFileName in ThisBuild := ".env"

lazy val root = (project in file("."))
  .settings(name := "final-project")
  .aggregate(user_activity_emulator, black_data_provider, streaming_app)

lazy val user_activity_emulator = (project in file("user-activity-emulator"))
  .enablePlugins(sbtdocker.DockerPlugin)
  .settings(
    name := "user-activity-emulator",
    libraryDependencies ++= commonDependencies ++ akkaDependencies ++ Seq(
      // https://mvnrepository.com/artifact/com.github.javafaker/javafaker
      "com.github.javafaker" % "javafaker" % "1.0.2"
    ),
    dockerSettings()
  ).dependsOn(common)

lazy val black_data_provider = (project in file("black-data-provider"))
  .enablePlugins(sbtdocker.DockerPlugin)
  .settings(
    name := "black-data-provider",
    libraryDependencies ++= commonDependencies ++ akkaDependencies ++ Seq(
      // your additional dependencies go here
    ),
    dockerSettings()
  ).dependsOn(common)

lazy val streaming_app = (project in file("streaming-app"))
  .enablePlugins(sbtdocker.DockerPlugin)
  .settings(
    name := "streaming-app",
    libraryDependencies ++= commonDependencies ++ streamsDependencies ++ Seq(
      // your additional dependencies go here

    ),
    dockerSettings(),
    mainClass in assembly := Some("ua.ucu.edu.DummyStreamingApp")
  ).dependsOn(common)

lazy val common = (project in file("common"))
  .settings(
    name := "common",
    libraryDependencies ++= commonDependencies ++ streamsDependencies ++ Seq(
      // your additional dependencies go here
    )
  )