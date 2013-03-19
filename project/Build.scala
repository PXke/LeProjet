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
    "leodagdag" %% "play2-morphia-plugin"  % "0.0.14",
    "jdom" % "jdom" % "1.1"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers ++= Seq(DefaultMavenRepository, Resolvers.githubRepository, Resolvers.morphiaRepository)      
  )
  
  object Resolvers {
    val githubRepository = "LeoDagDag repository" at "http://leodagdag.github.com/repository/"
    val dropboxRepository = "Dropbox repository" at "http://dl.dropbox.com/u/18533645/repository/"
    val morphiaRepository = "Morphia repository" at "http://morphia.googlecode.com/svn/mavenrepo/"
  }

}
