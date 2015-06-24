import sbt.Keys._

name := "sharedwhiteboard"

version := "0.4"

lazy val `sharedwhiteboard` = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq( javaJdbc , cache , javaWs )

//libraryDependencies += "org.mariadb.jdbc" % "mariadb-java-client" % "1.1.8"
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.18"



libraryDependencies ++= Seq(
  javaJpa.exclude("org.hibernate.javax.persistence", "hibernate-jpa-2.0-api"),
  "org.hibernate" % "hibernate-entitymanager" % "4.3.8.Final" // replace by your jpa implementation
)


unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  