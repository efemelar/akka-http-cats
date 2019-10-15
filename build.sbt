lazy val akkaHttpVersion = "10.1.10"
lazy val akkaVersion    = "2.5.25"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "efemelar",
      scalaVersion    := "2.13.1",
      scalacOptions ++= Seq("-feature", "-language:higherKinds", "-deprecation")
    )),
    name := "akka-http-cats",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"          % akkaVersion,

      "org.typelevel"     %% "cats-core"            % "2.0.0",

      "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
      "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"            % "3.0.8"         % Test
    ),
    Test/fork := true,
    run/fork := true
  )
