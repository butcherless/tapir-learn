# CLAUDE.md

## Project overview

A learning project exploring [Tapir](https://tapir.softwaremill.com/) with Pekko HTTP and ZIO HTTP interpreters. Versions are pinned in `project/Versions.scala` and `project/build.properties` (sbt 2). Two independent codebases live side by side, so don't assume conventions from one apply to the other:

- **`tapir-webapp`** (`com.cmartin.learn`): money-transfer and aircraft APIs, each exposed via Pekko HTTP and ZIO HTTP.
- **`aviation-*`** (`com.cmartin.aviation`): a layered ports & adapters rewrite modeling countries, airports and airlines.

## Commands

Run these from the sbt shell. From an OS shell, join multiple commands with `;` in one quoted string (`sbt "clean;compile"`); `sbt clean compile` fails to parse under sbt 2's thin client.

- `compile`, `~compile`, `reload`, `clean`
- `test`: under sbt 2 this skips tests it considers unchanged. Use `testFull` to run everything, as CI does.
- `testOnly com.cmartin.learn.api.ActuatorApiSpec`, optionally `-- -z "keyword"`
- `assembly`: fat jars (`aviation-api` → `aviation-webapp.jar`, `tapir-webapp` → `tapir-webapp.jar`)
- `xcoverage`: `clean;coverage;test;coverageReport`
- `xdup`: `dependencyUpdates`
- `scalafmtAll`: format (`.scalafmt.conf`)
- `./cleanup.sh`: removes `target`, `.bloop`, `.bsp`, `.idea`, `.vscode`, `.metals` everywhere

`reStart`/`reStop` are broken: `sbt-revolver` isn't in `project/plugins.sbt`, though the README documents it.

CI (`.github/workflows/scala.yml`) runs `scalafmtCheckAll`, `compile`, `Test/compile`, `testFull`, `assembly`, `xdup`.

### Running a server locally

```
sbt "tapir-webapp/assembly"
java -jar target/out/jvm/scala-<version>/tapir-webapp/tapir-webapp.jar
curl -v http://localhost:8080/api/v1.0/health | jq
```

That's the Pekko HTTP implementation, with Swagger UI at http://localhost:8080/docs. `aviation-api` works the same way. `aviation-web` (ZIO HTTP, port 8081) has no jar name override, so use `aviation-web-assembly-<version>.jar` or `sbt aviation-web/run`.

If an assembled jar crashes on its first request with `META-INF resources are missing`, check `assemblyMergeStrategy` in `build.sbt`: it must keep the swagger-ui webjar's `META-INF/resources` ([Tapir's merge strategy](https://tapir.softwaremill.com/en/latest/docs/openapi.html#using-swaggerui-with-sbt-assembly)).

### Live integration tests

`integration/src/test/scala/com/cmartin/learn/SttpITSpec.scala` makes real requests against a running `tapir-webapp`. It's left out of `aviation-root`'s aggregate so `compile`/`test` never need a server. Start the server, then run `sbt integration/test`.

## Architecture

### `aviation-*`

Dependency chain (see `build.sbt` and `docs/component-diagram.puml`):

```
aviation-core  <-- aviation-repository <-- aviation-service <-- aviation-api (Pekko HTTP)
     ^                                            ^
     +-------------------- aviation-test-utils ---+

aviation-web (ZIO HTTP, depends on aviation-api in build.sbt but is self-contained)
```

- **`aviation-core`**: the domain (`domain.Model`, `ServiceError` hierarchy) and port traits in `port`. It depends on no other module.
- **`aviation-repository`**: Slick persistence. Dbo case classes in `repository.Model`, ZIO-wrapped access in `zioimpl.*Repository`, `toDomain`/`toDbo` in `zioimpl.Mappers`, and an STM-backed `SetCountryRepository`.
- **`aviation-service`**: `*PersisterLive` implementations of the core ports, each a `ZLayer`.
- **`aviation-api`**: Pekko HTTP with Tapir (`*Endpoints`, `Api` classes, view models in `api.Model`, `CountryValidator`). **`ApiConfiguration` wires `CountryApi` to a stub `CountryService` (`???`), not `CountryPersisterLive`**, so api and service aren't connected end to end yet.
- **`aviation-web`**: ZIO HTTP with `ztapir`, a proof of concept with its own in-memory `ServiceLayer` and model.
- **`aviation-test-utils`**: shared test data and helpers.

Each layer has its own model (Dbo, domain, view) with extension-method conversions to the adjacent layer. Errors are mapped at each boundary (`ServiceError` → `ErrorInfo`/HTTP status via `manageError`).

### `tapir-webapp`

`domain.Model`/`domain.ApiConverters` hold the model and conversions. `api.*` holds endpoints and Pekko bindings (`api.*Api`) alongside ZIO HTTP bindings (`apizio.*Api`), served from `PekkoWebServerApp` and `ZioHttpServerApp`. It uses json4s for the Pekko JSON codec (`Json4sApi`) and zio-json elsewhere.

### Cross-cutting

- ZIO is used for effects even where the server is Pekko HTTP; effects run through `zio.Runtime` at the route boundary.
- Tests use ScalaTest (`AnyFlatSpec`), ScalaMock, and in-memory H2 for repository and persister tests.
- `sbt-buildinfo` generates build info for `aviation-api` and `tapir-webapp`; `sbt-git` supplies the version.
