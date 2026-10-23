plugins {
    kotlin("jvm") version "2.3.0"
    id("info.solidsoft.pitest") version "1.19.0"
}

group = "org.ikigaidigital"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    testImplementation(kotlin("test-junit5"))
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.23.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

pitest {
    junit5PluginVersion.set("1.2.2")
    targetClasses.set(setOf("org.ikigaidigital.*"))
    mutationThreshold.set(90)          // build fails below 90% killed mutants
    outputFormats.set(setOf("HTML", "XML"))
    timestampedReports.set(false)
}