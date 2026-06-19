# grails-micronaut-integration

Sample app for **Micronaut Integration in Grails 8** (Apache Grails `8.0.0-SNAPSHOT`, JDK 21 baseline / JDK 25 for Micronaut runtime).

The guide walks through adding Micronaut to a Grails REST API: applying `grails-micronaut-bom` as an `enforcedPlatform`, enabling the `grails-micronaut` plugin, declaring a Micronaut `@Client` HTTP interface, wiring it into a Grails service, and exposing search results through JSON views. Local `RecordLabel` data stays in GORM/PostgreSQL; album metadata comes from the iTunes Search API via Micronaut's declarative HTTP client.

## Layout

| Directory | What it is |
|-----------|------------|
| `initial/` | Vanilla Grails 8 REST API starter from [start.grails.org](https://start.grails.org) (`postgres`, `testcontainers`, `spock`). Work through the guide starting here. |
| `complete/` | The finished sample — `grails-micronaut` plugin, `grails-micronaut-bom`, declarative `ItunesClient`, `ItunesSearchService`, `RecordLabel` REST API, JSON views, and Spock unit + integration tests. |

## Running

```bash
git clone -b grails8 https://github.com/sanjana2505006/grails-micronaut-integration.git
cd grails-micronaut-integration/complete
./gradlew test integrationTest
```

To follow the guide step by step, start from `initial/`:

```bash
cd grails-micronaut-integration/initial
./gradlew test
```

Run the finished app (requires **JDK 25+** for Micronaut runtime):

```bash
cd grails-micronaut-integration/complete
./gradlew bootRun
```

Example endpoints: `GET /api/recordLabels`, `GET /api/search?q=U2`.

The integration spec (`RecordLabelIntegrationSpec`) asserts GORM persistence against a **real PostgreSQL database** (Testcontainers). `MicronautIntegrationSpec` verifies the declarative `@Client` bean is registered in the Micronaut context (skipped on JDK versions below 25).

Unit specs under `src/test/groovy/` cover domain constraints, service delegation to the Micronaut client, and controller behaviour with `DataTest` / `ControllerUnitTest`.

## Requirements

- **JDK 21** for `initial/` and for compiling `complete/` (the Gradle build enforces Java 21+)
- **JDK 25+** to run `complete/` with Micronaut (`bootRun`) or to execute `MicronautIntegrationSpec`
- **Docker** running locally — required for `integrationTest` (Testcontainers PostgreSQL). Unit tests (`./gradlew test`) do not need Docker.
- **PostgreSQL** on `localhost:5432` — required for `./gradlew bootRun` (default database `devDb` in `application.yml`)

If Gradle reports *"Run this build using a Java 21 or newer JVM"*, your shell or IDE is still on an older JDK:

```bash
sdk install java 21.0.6-tem
sdk default java 21.0.6-tem
java -version   # should show 21.x
```

For Micronaut runtime features, switch to JDK 25:

```bash
sdk install java 25-tem
sdk default java 25-tem
java -version   # should show 25.x
```

If `integrationTest` fails with a Testcontainers / `docker.sock` error, start Docker Desktop (or your local Docker daemon) and retry.

## The pattern, in one place

```groovy
// 1. Apply grails-micronaut-bom as enforcedPlatform in build.gradle:
implementation enforcedPlatform("org.apache.grails:grails-micronaut-bom:$grailsVersion")
implementation "org.apache.grails:grails-micronaut"
implementation "io.micronaut:micronaut-http-client-jdk"

// 2. Declare a Micronaut HTTP client interface:
@Client('https://itunes.apple.com/')
interface ItunesClient {
    @Get('/search?limit=25&media=music&entity=album&term={term}')
    SearchResult search(String term)
}

// 3. Inject the client into a Grails service:
@Autowired
ItunesClient itunesClient

List<Album> searchAlbums(String searchTerm) {
    itunesClient.search(searchTerm.trim())?.results ?: []
}
```

Controllers stay thin and delegate to services; JSON views under `grails-app/views/` shape REST responses.

## Guide prose

Published narrative lives on [grails.apache.org/guides](https://grails.apache.org/guides/) in [apache/grails-static-website](https://github.com/apache/grails-static-website) under `guides/grails-micronaut-integration/v8/`.

## CI

GitHub Actions (`.github/workflows/grails8.yml`) runs `./gradlew test` for `initial` and `complete`, and `./gradlew integrationTest` for `complete`, on pushes and PRs to the `grails8` branch.
