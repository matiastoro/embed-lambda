#to compile
sbt package

#to run test.scala example
scalac test.scala -cp target/scala-2.10/embed-lambda-plugin_2.10-0.1-SNAPSHOT.jar -Xplugin:target/scala-2.10/embed-lambda-plugin_2.10-0.1-SNAPSHOT.jar