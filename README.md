# tapir-learn 
Learning to build APIs with Scala Tapir

[![CircleCI](https://circleci.com/gh/butcherless/tapir-learn.svg?style=svg)](https://circleci.com/gh/butcherless/tapir-learn)

![Scala CI](https://github.com/butcherless/tapir-learn/workflows/Scala%20CI/badge.svg)

## Quick start up

Run the following commands for quick startup and check

1. git clone https://github.com/butcherless/tapir-learn.git
2. cd tapir-learn
3. sbt
4. reStart
5. http://localhost:8080/api/v1.0/docs


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

http://localhost:8080/docs

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


## Testing

Rapid test cycle via continuous testing with a single suite test:

    ~testOnly com.cmartin.learn.api.ActuatorApiSpec

Rapid test cycle via continuous testing with a single test:

    ~testOnly com.cmartin.learn.api.ActuatorApiSpec -- -z "keyword"

## Integration (httpie client)

    http "http://localhost:8080/api/v1.0/health"
    
    http "http://localhost:8080/api/v1.0/transfers"
    
    http "http://localhost:8080/api/v1.0/acEntity"

Links:

- http://www.scalatest.org/at_a_glance/FlatSpec
- https://doc.akka.io/docs/akka-http/current/routing-dsl/testkit.html

## Logback config

Asynchronous non-blocking _appender_ config

- http://logback.qos.ch/manual/appenders.html

## Akka config

Basic standard configuration

https://doc.akka.io/docs/akka/current/general/configuration.html

    src/main/resources/application.conf

## Tapir docs

https://tapir-scala.readthedocs.io/en/latest/index.html

## Experimental

CI tasks with Seed build tool:

    docker run -it tindzk/seed:0.1.6 /bin/sh
    apk add git
    cd
    git clone https://github.com/butcherless/tapir-learn.git
    cd tapir-learn
    bloop server &
    seed bloop
    bloop test tapirlearn
