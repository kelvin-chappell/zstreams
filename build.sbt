ThisBuild / scalaVersion := "2.13.8"

val zioVersion = "2.0.0-RC6"

lazy val root = (project in file("."))
  .settings(
    name := "zstreams",
    libraryDependencies ++= Seq(
      "dev.zio"  %% "zio"                        % zioVersion,
      "dev.zio"  %% "zio-streams"                % zioVersion,
      "com.gu"   %% "content-api-client-default" % "18.0.1",
      "org.slf4j" % "slf4j-nop"                  % "1.7.36" % Runtime,
    ),
  )
