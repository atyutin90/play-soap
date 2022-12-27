/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */

libraryDependencies := Seq(
  "com.typesafe.play" % "play-soap-plugin" % "1.2.0+208-fe91303e+20221128-1810-SNAPSHOT",
  "net.aichler" % "jupiter-interface" % "0.11.1" % Test
)

addSbtPlugin("de.heikoseeberger" % "sbt-header" % "5.9.0")

addSbtPlugin("org.scalameta"     % "sbt-scalafmt"       % "2.5.0")
addSbtPlugin("com.lightbend.sbt" % "sbt-java-formatter" % "0.8.0")
addSbtPlugin("io.paymenthighway.sbt" % "sbt-cxf" % "1.6")
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.9.9")
addSbtPlugin("net.aichler" % "sbt-jupiter-interface" % "0.11.1")

// Releasing
addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.11")
