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
    "jdom" % "jdom" % "1.1"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers ++= Seq(DefaultMavenRepository, Resolvers.morphiaRepository)      
  )
  
  object Resolvers {
    val morphiaRepository = "Morphia repository" at "http://morphia.googlecode.com/svn/mavenrepo/"
  }

}
