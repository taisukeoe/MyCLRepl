name := "MyClassLoaderREPL"

lazy val versionText = "2.11.4"

scalaVersion := versionText

version := "0.1-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-compiler" % versionText
)