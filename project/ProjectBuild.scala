import sbt._
import Keys._

object ProjectBuild extends Build {


  val sharedSettings = Seq (
    scalaVersion := "2.10.1",
    libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-compiler" % _ )

  )

  lazy val pluginProject: Project = Project(id = "embed-lambda-plugin", base = file(".")) settings (
    name := "embed-lambda-plugin"

  ) settings (sharedSettings: _*)


}
