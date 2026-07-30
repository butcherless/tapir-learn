# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

A Scala/sbt learning project exploring the [Tapir](https://tapir-scala.readthedocs.io/) library for defining
HTTP APIs, with both Akka HTTP and ZIO HTTP server interpreters. The repo contains two independent codebases:

- **`tapir-webapp`** — the original module (package `com.cmartin.learn`). Money-transfer and aircraft APIs,
  each exposed twice: once via Akka HTTP (`AkkaWebServerApp`) and once via ZIO HTTP (`ZioHttpServerApp`).
- **`aviation-*`** modules — a newer, layered rewrite (package `com.cmartin.aviation`) modeling countries,
  airports and airlines, structured as ports & adapters across five sbt modules (see Architecture below).

Both codebases are independent learning exercises living side by side; don't assume conventions from one
apply to the other.

## Commands

All commands are run from the sbt shell (`sbt` at the repo root) unless noted otherwise. This project runs
on **sbt 2** (`project/build.properties`) — when invoking multiple commands from an OS-shell one-liner
rather than the interactive shell, join them with `;` in one quoted string (`sbt "clean;compile"`); bare
space-separated commands (`sbt clean compile`) fail to parse under sbt 2's thin client.

- `compile` / `~compile` — compile (continuous with `~`)
- `test` / `~test` — run all test suites
- `testOnly com.cmartin.learn.api.ActuatorApiSpec` — run a single spec
- `~testOnly com.cmartin.learn.api.ActuatorApiSpec -- -z "keyword"` — continuous run of tests matching `keyword`
  within a spec (ScalaTest `-z` substring filter)
- `<module>/reStart`, `reStop`, `reStatus`, `~reStart` — run/manage a server app with sbt-revolver, e.g.
  `aviation-api/reStart`, `aviation-web/reStart`, `tapir-webapp/reStart`
- `reload` — reload sbt after editing `build.sbt`
- `clean` — wipe `target` directories
- `assembly` — build a fat jar for a module (`aviation-api` → `aviation-webapp.jar`, `tapir-webapp` →
  `tapir-webapp.jar`)
- `xcoverage` — alias for `clean;coverage;test;coverageReport` (scoverage)
- `xdup` / `dependencyUpdates`, `dependencyBrowseTree`, `dependencyList`, `dependencyTree` — dependency
  inspection (sbt-updates / sbt-dependency-graph)
- `scalafmt` / `scalafmtAll` — format code (config in `.scalafmt.conf`, max column 120)
- `./cleanup.sh` — removes all `target`, `.bloop`, `.bsp`, `.idea`, `.vscode`, `.metals` directories repo-wide

CI (`.github/workflows/scala.yml`) runs, in order: `compile`, `Test/compile`, `test`, `assembly`, `xdup`, on
JDK 21 (Zulu).

### Running a server locally

- `aviation-api/reStart` → Akka HTTP implementation, Swagger UI at http://localhost:8080/docs
- `aviation-web/reStart` → ZIO HTTP implementation, Swagger UI at http://localhost:8081/docs
- `tapir-webapp/reStart` → Swagger UI at http://localhost:8080/docs
- Health check: `curl -v http://localhost:8080/api/v1.0/health | jq`

## Architecture

### `aviation-*` modules — ports & adapters

Module dependency chain (see `build.sbt` and `docs/component-diagram.puml`):

```
aviation-core  <-- aviation-repository <-- aviation-service <-- aviation-api
     ^                                            ^                (Akka HTTP)
     |                                            |
     +-------------------- aviation-test-utils ---+           aviation-web
                                                                (ZIO HTTP, depends on aviation-api
                                                                 in build.sbt but currently self-contained,
                                                                 see below)
```

- **`aviation-core`** — the domain: `domain.Model` (`Country`, `Airport`, `Airline`, `ServiceError` hierarchy)
  and the *ports* — traits in the `port` package (`CountryService`, `CountryPersister`, `AirportPersister`,
  `AirlinePersister`, `CountryCrudRepository`) that downstream layers implement or depend on. No dependency on
  any other module.
- **`aviation-repository`** — Slick-based persistence. `repository.Model` holds Dbo (database object)
  case classes; `zioimpl.Tables` / `zioimpl.*Repository` wrap Slick access in ZIO; `zioimpl.Mappers` provides
  `toDomain`/`toDbo` extension conversions between `aviation-core` domain models and Dbo models. An
  in-memory ZIO STM-backed variant also exists (`SetCountryRepository`). Depends on `aviation-core`.
- **`aviation-service`** — "Live" implementations of the `aviation-core` persister ports (e.g.
  `CountryPersisterLive`), each exposed as a `ZLayer` (`CountryPersisterLive.layer`) built from the
  corresponding repository. This is the layer that fulfils the port contracts defined in `aviation-core`
  using `aviation-repository`. Depends on `aviation-core`, `aviation-repository`, and
  `aviation-test-utils` (test scope only).
- **`aviation-api`** — Akka HTTP server using tapir endpoints (`*Endpoints`), `Api` classes that bind
  endpoints to server logic, and view models (`api.Model`) with `toView`/domain-conversion extension
  methods, mirroring the repository layer's Dbo mapping pattern. `CountryValidator` validates/converts
  incoming view requests to domain models. **Note:** `ApiConfiguration` currently wires `CountryApi` to a
  stub `CountryService` (`???` bodies) rather than the real `CountryPersisterLive` from `aviation-service` —
  the api layer and service layer are not yet connected end-to-end.
- **`aviation-web`** — ZIO HTTP server using tapir's `ztapir` DSL (`ApiLayer`, `ServiceLayer`,
  `ZioHttpServer`). Currently a self-contained proof of concept with its own in-memory `ServiceLayer` and
  domain-like model rather than using `aviation-core`/`aviation-service`, despite the module dependency on
  `aviation-api` in `build.sbt`.
- **`aviation-test-utils`** — shared test data/helpers (`TestData`, `Helper`, `Common`), depended on by
  `aviation-service` tests.

Recurring pattern across layers: each layer defines its own model (Dbo in `aviation-repository`, domain in
`aviation-core`, view in `aviation-api`) with implicit-class extension methods for converting to/from the
adjacent layer's model, and errors are mapped at each boundary (`ServiceError` in the domain →
Api-layer `ErrorInfo`/HTTP status codes via `manageError` functions).

### `tapir-webapp` module

Package `com.cmartin.learn`. Independent of the `aviation-*` stack. `domain.Model` /
`domain.ApiConverters` hold the domain model and view conversions; `api.*` holds tapir endpoint
definitions (`*Endpoint`) and their Akka HTTP (`api.*Api`) and ZIO HTTP (`apizio.*Api`) bindings side by
side, both served from the same `AkkaWebServerApp` / `ZioHttpServerApp` entry points. Uses json4s for the
Akka HTTP JSON codec (`Json4sApi`) and zio-json elsewhere.

### Cross-cutting notes

- Scala 2.13, sbt multi-module build (`aviation-root` aggregates all modules).
- ZIO is used throughout for effect handling (`aviation-core`, `aviation-repository`, `aviation-service`,
  `aviation-web`) even where the HTTP server itself is Akka HTTP (`aviation-api`, `tapir-webapp`) — ZIO
  effects are run via `zio.Runtime` / `run(...)` helpers at the Akka route boundary, and `ZLayer`s wire
  persister/repository implementations together.
- Tests use ScalaTest (`AnyFlatSpec` style) with ScalaMock for mocking ports/services and an in-memory H2
  database for repository/persister-level tests.
- `sbt-buildinfo` generates build info objects for `aviation-api` (`AviationBuildInfoSettings`) and
  `tapir-webapp` (`BuildInfoSettings`); `sbt-git` supplies the version via `GitVersioning`.
