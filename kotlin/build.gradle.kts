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

extra["testcontainersVersion"] = "2.0.5"
extra["junit-jupiter.version"] = "6.1.3"
extra["testcontainers.version"] = property("testcontainersVersion") as String

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
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
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

val openApiGeneratedDir =
    layout.buildDirectory
        .dir("generated/openapi")
        .get()
        .asFile

openApiGenerate {
    generatorName.set("kotlin-spring")
    inputSpec.set(file("src/main/resources/openapi/time-deposit-api.yaml").toURI().toString())
    outputDir.set(openApiGeneratedDir.path)
    apiPackage.set("org.ikigaidigital.adapters.web.generated")
    modelPackage.set("org.ikigaidigital.adapters.web.generated.model")
    configOptions.set(
        mapOf(
            "interfaceOnly" to "true",
            "useTags" to "true",
            "useSpringBoot3" to "true",
            "documentationProvider" to "none",
            "annotationLibrary" to "none",
            "generateJsonIncludeAnnotations" to "false",
            "generateJsonSetterNullsAnnotations" to "false",
        ),
    )
}

sourceSets {
    main {
        kotlin {
            srcDir(openApiGeneratedDir.resolve("src/main/kotlin"))
        }
    }
}

tasks.named("compileKotlin") {
    dependsOn("openApiGenerate")
}

ktlint {
    filter {
        exclude { entry -> entry.file.path.contains(openApiGeneratedDir.path) }
    }
}

tasks.matching { it.name.startsWith("runKtlint") && it.name.contains("Main") }.configureEach {
    dependsOn("openApiGenerate")
}

springBoot {
    mainClass.set("org.ikigaidigital.adapters.TimeDepositApplicationKt")
}

pitest {
    junit5PluginVersion.set("1.2.2")
    targetClasses.set(setOf("org.ikigaidigital.*"))
    // Pure Spring wiring/bootstrap glue has no meaningful behavior to mutate --
    // it's framework configuration, not domain/application logic
    excludedClasses.set(
        setOf(
            "org.ikigaidigital.adapters.config.*",
            "org.ikigaidigital.adapters.TimeDepositApplicationKt",
            "org.ikigaidigital.adapters.web.generated.*",
        ),
    )
    mutationThreshold.set(90) // build fails below 90% killed mutants
    outputFormats.set(setOf("HTML", "XML"))
    timestampedReports.set(false)
}
