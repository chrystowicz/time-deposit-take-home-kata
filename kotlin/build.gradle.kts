plugins {
    kotlin("jvm") version "2.3.0"
    id("info.solidsoft.pitest") version "1.19.0"
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
}

group = "org.ikigaidigital"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    testImplementation(kotlin("test-junit5"))
    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("org.mockito.kotlin:mockito-kotlin:6.4.0")
    testImplementation("io.kotest:kotest-runner-junit5:6.2.6")
    testImplementation("io.kotest:kotest-assertions-core:6.2.6")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

pitest {
    junit5PluginVersion.set("1.2.2")
    targetClasses.set(setOf("org.ikigaidigital.*"))
    mutationThreshold.set(90) // build fails below 90% killed mutants
    outputFormats.set(setOf("HTML", "XML"))
    timestampedReports.set(false)
}
