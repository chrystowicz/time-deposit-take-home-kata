plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.spring") version "2.3.0"
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("info.solidsoft.pitest") version "1.19.0"
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
}

group = "org.ikigaidigital"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

extra["testcontainersVersion"] = "1.21.3"
// Spring Boot's dependency-management plugin pins JUnit via this exact property
// (its BOM imports org.junit:junit-bom using it internally). Without this override,
// it manages junit-platform-engine/-jupiter down to the old 5.12.x/1.12.x line while
// junit-platform-launcher resolves to the junit-bom version below -- a split that
// crashes the test runner (NoClassDefFoundError: OutputDirectoryCreator). A plain
// platform()/enforcedPlatform() import does not override this, since Spring applies
// it as a dependency constraint, not a competing recommendation.
extra["junit-jupiter.version"] = "6.1.3"
extra["testcontainers.version"] = property("testcontainersVersion") as String

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Scaffolding only: a JDBC datasource to Postgres, no JPA/entities, no
    // controllers yet. spring-boot-starter-web is still needed even with zero
    // @RestController classes -- without it there's no embedded web server to
    // keep the JVM alive, so the app boots and exits immediately instead of
    // actually running as a service.
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation(kotlin("test-junit5"))
    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("org.mockito.kotlin:mockito-kotlin:6.4.0")
    testImplementation("io.kotest:kotest-runner-junit5:6.2.6")
    testImplementation("io.kotest:kotest-assertions-core:6.2.6")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation(platform("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    // Testcontainers 1.21.x's bundled docker-java falls back to a hardcoded,
    // long-obsolete default API version (1.32) whenever it can't resolve one
    // from config, which newer Docker Engine releases reject outright. The
    // DOCKER_API_VERSION environment variable is NOT read by this client --
    // only the api.version JVM system property is. 1.44 is Docker Engine's
    // current stated minimum supported API version, so any daemon new enough
    // to run today still accepts it.
    systemProperty("api.version", "1.44")
}

springBoot {
    mainClass.set("org.ikigaidigital.adapters.TimeDepositApplicationKt")
}

pitest {
    junit5PluginVersion.set("1.2.2")
    targetClasses.set(setOf("org.ikigaidigital.*"))
    // Pure Spring wiring/bootstrap glue has no meaningful behavior to mutate --
    // it's framework configuration, not domain/application logic. Narrowed to
    // just the @Configuration classes and the @SpringBootApplication entry
    // point; adapters.persistence/adapters.web now hold real logic (SQL,
    // controller behavior) and are deliberately NOT excluded.
    excludedClasses.set(
        setOf(
            "org.ikigaidigital.adapters.config.*",
            "org.ikigaidigital.adapters.TimeDepositApplicationKt",
        ),
    )
    mutationThreshold.set(90) // build fails below 90% killed mutants
    verbose.set(true)
    outputFormats.set(setOf("HTML", "XML"))
    timestampedReports.set(false)
}
