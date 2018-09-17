name := "KudosCounter"
 
version := "1.0" 
      
lazy val `kudoscounter` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

/* ReactiveMongo */
libraryDependencies += "org.reactivemongo" %% "play2-reactivemongo" % "0.12.7-play26"
libraryDependencies += "org.reactivemongo" %% "reactivemongo-akkastream" % "0.12.7"


//import play.sbt.routes.RoutesKeys

//RoutesKeys.routesImport += "play.modules.reactivemongo.PathBindables._"


/*Play Json*/
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.10"

/*Play WS*/

libraryDependencies += ws

/* Slack Client*/
libraryDependencies += "com.github.gilbertw1" %% "slack-scala-client" % "0.2.3"