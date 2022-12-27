/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */
import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd}
import Dependencies.ScalaVersions._

lazy val root = project
  .in(file("."))
  .aggregate(client, plugin, example)
  .settings(
    name               := "play-soap",
    crossScalaVersions := Nil,
    publish / skip     := true
  )

lazy val client = project
  .in(file("client"))
  .enablePlugins(CxfPlugin)
  .settings(
    name               := "play-soap-client",
    description        := "Play SOAP client",
    crossScalaVersions := Seq(scala213),
    Dependencies.`play-client`,
  )
  .settings(
  Test / testOptions += Tests.Argument(jupiterTestFramework, "-q", "-v"))
  .settings(
    cxfWSDLs := Seq(
      Wsdl(
        "HelloWorld",
        (example / Compile / resourceDirectory).value / "wsdl/helloWorld.wsdl",
        Seq("-fe", "play", "-p", wsTestPackage, "-xjc-Xplay:lang java"))
    )
  )
  .settings((Test / test) := {
    (Test / test) dependsOn (buildDocker)
  }.value)

lazy val plugin = project
  .in(file("plugin"))
  .settings(
    name        := "play-soap-plugin",
    description := "Play SOAP plugin for wsdl2java",
    Dependencies.plugin,
    crossPaths       := false,
    autoScalaLibrary := false
  )

lazy val example = project
  .in(file("example"))
  .enablePlugins(JavaAppPackaging, DockerPlugin, CxfPlugin)
  .settings(
    name := "play-soap-example",
    description := "Play SOAP example (Using for test).",
    crossPaths := false,
    autoScalaLibrary := false)
  .settings(
    ThisBuild / buildDocker := (Docker / publishLocal).value,
    Docker / packageName := "play/soap-test-server",
    Docker / version := "0.0.0",
    dockerCommands := Seq(
      Cmd("FROM", "bellsoft/liberica-openjdk-alpine:8u275-1"),
      Cmd("COPY", "*/opt/docker/lib/", "/opt/lib/"),
      Cmd("EXPOSE", "8080"),
      ExecCmd("ENTRYPOINT", "java", "-cp", "/opt/lib/*", "play.soap.HelloWorld_HelloWorld_Server")
    ))
  .settings(
    cxfWSDLs := Seq(
      Wsdl(
        "HelloWorld",
        (Compile / resourceDirectory).value / "wsdl/helloWorld.wsdl",
        Seq("-server", "-impl", "-p", wsTestPackage, "-wsdlLocation", "wsdl/helloWorld.wsdl")
      )
    )
  )

lazy val buildDocker = taskKey[Unit]("Build Example Docker")
lazy val wsTestPackage = "play.soap"

// Customise sbt-dynver's behaviour to make it work with tags which aren't v-prefixed
(ThisBuild / dynverVTagPrefix) := false

// Sanity-check: assert that version comes from a tag (e.g. not a too-shallow clone)
// https://github.com/dwijnand/sbt-dynver/#sanity-checking-the-version
Global / onLoad := (Global / onLoad).value.andThen { s =>
  dynverAssertTagVersion.value
  s
}
