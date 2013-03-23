import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "LeProjet"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    //javaJdbc,
    //javaEbean,
    "jdom" % "jdom" % "1.1",
	"com.github.jmkgreen.morphia" % "morphia" % "1.2.2",
      ("com.github.jmkgreen.morphia" % "morphia-logging-slf4j" % "1.2.2" % "compile" notTransitive())
        .exclude("org.slf4j", "slf4j-simple")
        .exclude("org.slf4j", "slf4j-jdk14"),
      ("com.github.jmkgreen.morphia" % "morphia-validation" % "1.2.2" % "compile" notTransitive())
        .exclude("org.slf4j", "slf4j-simple")
        .exclude("org.slf4j", "slf4j-jdk14")
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers ++= Seq(DefaultMavenRepository, Resolvers.morphiaRepository, Resolvers.typesafeRepository)      
  )
  
  object Resolvers {
    val morphiaRepository = "Morphia repository" at "http://morphia.googlecode.com/svn/mavenrepo/"
	val typesafeRepository = "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
  }

}
