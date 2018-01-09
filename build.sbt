name := "ck"

version := "0.1"

scalaVersion := "2.12.4"

lazy val root = (project in file("."))
  .aggregate(core)

lazy val core = (project in file("ck-core"))
