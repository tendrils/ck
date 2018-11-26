
lazy val ck = (project in file("."))
  .aggregate(core_mk2)

lazy val core_mk1 = project
lazy val core_mk2 = project
