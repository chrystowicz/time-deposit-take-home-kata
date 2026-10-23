plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.spring") version "2.3.0"
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("info.solidsoft.pitest") version "1.19.0"
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
    id("org.openapi.generator") version "7.26.0"
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
    // Compile-time only: the openapi-generator kotlin-spring templates emit
    // @Validated/ConstraintViolationException references unconditionally
    // (independent of whether the spec has any validated parameters). No
    // validation provider is pulled in since nothing in this API triggers it.
    implementation("jakarta.validation:jakarta.validation-api")
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

// Contract-first: src/main/resources/openapi/time-deposit-api.yaml is the
// single source of truth for the API shape. This generates a Spring MVC
// interface (routing annotations, no implementation) per path, which
// TimeDepositController implements. Changing an existing operation's
// signature here forces a compile error in the controller; it does NOT by
// itself force every operation to be implemented (kotlin-spring's generated
// interfaces have default, non-abstract method bodies) -- see
// TimeDepositControllerContractTest for that guard.
val openApiGeneratedDir =
    layout.buildDirectory
        .dir("generated/openapi")
        .get()
        .asFile

openApiGenerate {
    generatorName.set("kotlin-spring")
    // toURI().toString() (a file:/// URL), not plain toString(): the plugin's
    // relative-path resolution for inputSpec is otherwise inconsistent
    // between `openApiGenerate` and `openApiValidate`/IDE invocations.
    inputSpec.set(file("src/main/resources/openapi/time-deposit-api.yaml").toURI().toString())
    outputDir.set(openApiGeneratedDir.path)
    apiPackage.set("org.ikigaidigital.adapters.web.generated")
    modelPackage.set("org.ikigaidigital.adapters.web.generated.model")
    configOptions.set(
        mapOf(
            // Generates an interface with the routing annotations only --
            // no controller/business logic -- for TimeDepositController to
            // implement, rather than a full generated controller.
            "interfaceOnly" to "true",
            "useTags" to "true",
            "useSpringBoot3" to "true",
            // No springdoc/swagger-annotations dependency in this project --
            // skip generating @Operation/@ApiResponse annotations and the
            // SpringDocConfiguration helper class that would otherwise need it.
            "documentationProvider" to "none",
            "annotationLibrary" to "none",
            "generateJsonIncludeAnnotations" to "false",
            "generateJsonSetterNullsAnnotations" to "false",
        ),
    )
}

// Deliberately NOT added via sourceSets.main.kotlin.srcDir: that would also
// put this generated code in front of ktlint (which iterates registered
// sourceSets), forcing either lint failures on code we don't own or a filter
// workaround. Instead, only compileKotlin is told about the extra sources --
// ktlint never sees this directory at all.
tasks.named<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileKotlin") {
    dependsOn("openApiGenerate")
    source(openApiGeneratedDir.resolve("src/main/kotlin"))
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
            // openapi-generator's own boilerplate (exception handling,
            // ApiUtil, and the generated Api interfaces' default 501 method
            // bodies) -- not code we wrote, nothing meaningful to mutate.
            // Confirmed via a real pitest run before this exclusion existed:
            // 3 survived mutants, all inside this package. Once the spec
            // grows request/response bodies, generated model classes will
            // land under adapters.web.generated.model -- verify this glob
            // still covers them (or exclude that subpackage explicitly).
            "org.ikigaidigital.adapters.web.generated.*",
        ),
    )
    mutationThreshold.set(90) // build fails below 90% killed mutants
    // PIT forks its own JVM ("minion") for the coverage/mutation runs, which
    // does NOT inherit tasks.test's systemProperty -- without this, the
    // Testcontainers-backed tests (SpringContextIntegrationTest,
    // JdbcTimeDepositRepositoryTest, UpdateBalancesEndpointAcceptanceTest)
    // silently report zero coverage under PIT (DockerAvailableCondition
    // correctly, but misleadingly, treats Docker as unreachable there).
    jvmArgs.set(listOf("-Dapi.version=1.44"))
    outputFormats.set(setOf("HTML", "XML"))
    timestampedReports.set(false)
}
