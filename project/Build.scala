import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin

object Common extends AutoPlugin {
  override def trigger = allRequirements
  override def requires = JvmPlugin

  override def projectSettings = Seq(
    organization := "com.typesafe.play",
    resolvers += Resolver.typesafeRepo("releases")
  )

  object autoImport {
    val CxfVersion = "3.0.3"
    val PlayVersion = "2.3.7"
  }
}
