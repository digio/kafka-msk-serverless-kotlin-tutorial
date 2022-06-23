
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("com.github.davidmc24.gradle.plugin.avro-base") version "1.3.0"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
}

group = "org.kafka.tutorial"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven {
        url = uri("https://packages.confluent.io/maven")
    }
}

extra["testcontainersVersion"] = "1.17.2"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.apache.kafka:kafka-streams")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.23")
    implementation("software.amazon.msk:aws-msk-iam-auth:1.1.4")
    implementation("software.amazon.glue:schema-registry-serde:1.1.10")
    implementation("org.apache.avro:avro:1.11.0")
    implementation("org.apache.avro:avro-tools:1.11.0")
    implementation("io.confluent:kafka-avro-serializer:7.1.1")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:kafka")
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
    }
}

var generateAvro = tasks.register<com.github.davidmc24.gradle.plugin.avro.GenerateAvroJavaTask>("generateAvro") {
    source("src/main/resources/avro")
    setOutputDir(File("src/main/java"))
}

tasks.withType<KotlinCompile> {
    source(generateAvro)
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

avro {
    stringType.set("CharSequence")
    fieldVisibility.set("private")
}
