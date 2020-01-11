# tapir-learn
Learning to build APIs with Scala Tapir

## Quick start up

Run the following commands for quick startup and check

0. git clone https://github.com/butcherless/tapir-learn.git
0. cd tapir-learn
0. sbt
0. reStart
0. http://localhost:8080/api/v1.0/docs


## Project structure

Sbt build tool config file

    build.sbt

JVM options for Sbt
    
    .jvmopts

Sbt version
    
    project/build.properties

Project dependencies

    project/Dependencies

Sbt plugins

    project/plugins.sbt


## Compile and run this project

Run Sbt build tool

`sbt` &#9166;

Compile the application 

`compile` or `~compile` for continuous compiling

Start, stop the web server

`reStart`, `reStop`, `reStatus`, or `~reStart`

Check Swagger API

http://localhost:8080/api/v1.0/docs

Check Health endpoint

http://localhost:8080/api/v1.0/health

curl -v http://localhost:8080/api/v1.0/health | jq

Run the test suites

`test` or `~test` for continuous testing

Reload changes in _build.sbt_ config file

`reload`

Clean `target` working directory

`clean`

Check dependencies with command line and browser

`dependencyUpdates`, `dependencyBrowseTree`, `dependencyList`, `dependencyTree`

Exit Sbt

`CTRL + D`


## Logback config

Asynchronous non-blocking _appender_ config

## Akka config

Basic standard configuration

https://doc.akka.io/docs/akka/current/general/configuration.html

    src/main/resources/application.conf

## Tapir docs

https://tapir-scala.readthedocs.io/en/latest/index.html

