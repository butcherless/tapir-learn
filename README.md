# tapir-learn 
Learning to build APIs with Scala Tapir

![Scala CI](https://github.com/butcherless/tapir-learn/workflows/Scala%20CI/badge.svg)

[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)

## Quick start up

Run the following commands for quick startup and check

1. git clone https://github.com/butcherless/tapir-learn.git
2. cd tapir-learn
3. sbt
4. aviation-api/reStart
5. http://localhost:8080/docs
6. aviation-web/reStart
7. http://localhost:8081/docs
7. tapir-webapp/reStart
8. http://localhost:8080/docs

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

> **sbt 2 note:** the commands below are meant to be typed one at a time inside the interactive
> `sbt` shell. If you invoke sbt from the OS shell with several bare commands in one line (e.g.
> `sbt clean compile`), sbt 2's thin client will fail to parse it (`Expected whitespace character`).
> Join them with `;` in a single quoted string instead: `sbt "clean;compile"`.

Compile the application 

`compile` or `~compile` for continuous compiling

Manage the web server with Revolver plugin

`reStart`, `reStop`, `reStatus`, or `~reStart`

> **Currently broken:** `sbt-revolver` is not in `project/plugins.sbt`, so `reStart` fails with
> `Not a valid key: reStart`. Until it's restored, run a module's server via its assembled jar
> instead (also exercises the `assembly` task):
> `sbt "<module>/assembly"` then `java -jar target/out/jvm/scala-3.9.0/<module>/<module>.jar`
> (or `<jarName>` from `assembly / assemblyJarName` if it differs from the module name).

Check Swagger API:

- `Pekko` implementation (`aviation-api` module): http://localhost:8080/docs
- `ZIO` implementation (`aviation-web` odule): http://localhost:8081/docs/

Check Health endpoint (Pekko)

http://localhost:8080/api/v1.0/health

```bash
curl -v http://localhost:8080/api/v1.0/health | jq
```

Run the test suites

`test` or `~test` for continuous testing

Reload changes in _build.sbt_ config file

    reload

Clean `target` working directory

    clean

Check dependencies with command line and browser

`dependencyUpdates`, `dependencyBrowseTree`, `dependencyList`, `dependencyTree`

Exit Sbt

    CTRL + D


## Testing

Rapid test cycle via continuous testing with a single suite test:

    ~testOnly com.cmartin.learn.api.ActuatorApiSpec

Rapid test cycle via continuous testing with a single test:

    ~testOnly com.cmartin.learn.api.ActuatorApiSpec -- -z "keyword"

## Integration

Automated: the `integration` sbt subproject (`integration/src/test/scala/com/cmartin/learn/SttpITSpec.scala`)
runs the same checks below as real sttp requests against a running server. It's intentionally left out of
the default `aviation-root` aggregate (`compile`/`test` won't touch it), so run it explicitly:

1. Start the server (see "Currently broken" note above for the `reStart` workaround), then
2. `sbt integration/test`

Manual: module: `tapir-webapp/reStart`

bash command: `httpie`

```bash
http -v "http://localhost:8080/api/v1.0/health"
```
```bash    
http -v "http://localhost:8080/api/v1.0/transfers/1"
 ```
```bash
http -v "http://localhost:8080/api/v1.0/transfers/404"
```
```bash   
http -v "http://localhost:8080/api/v1.0/transfers/500"
```    
```bash
echo '{"sender":"ES11 0182 1111 2222 3333 4444",
       "receiver":"ES99 2038 9999 8888 7777 6666",
       "amount":100.0,
       "currency":"EUR",
       "date":"2020-11-07T08:05:13.345Z",
       "desc":"Viaje a Tenerife"}' \
 |  http -v http://localhost:8080/api/v1.0/transfers
```

## Logback config

Asynchronous non-blocking _appender_ config

- http://logback.qos.ch/manual/appenders.html

## Pekko config

Basic standard configuration

https://pekko.apache.org/docs/pekko/current/general/configuration.html

    tapir-webapp/src/main/resources/application.conf


## Links:

- http://www.scalatest.org/at_a_glance/FlatSpec
- https://pekko.apache.org/docs/pekko-http/current/routing-dsl/testkit.html
- https://tapir-scala.readthedocs.io/en/latest/index.html
