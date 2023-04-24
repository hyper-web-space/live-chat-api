import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
  id("org.springframework.boot") version "3.0.5"
  id("io.spring.dependency-management") version "1.1.0"
  kotlin("jvm") version "1.7.22"
  kotlin("plugin.spring") version "1.7.22"
  id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
  id("jacoco")
}

group = "chung.me"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

jacoco {
  toolVersion = "0.8.8"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
  implementation("org.springframework.boot:spring-boot-starter-data-redis")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.springframework.boot:spring-boot-starter-websocket")
  developmentOnly("org.springframework.boot:spring-boot-devtools")
  implementation("io.jsonwebtoken:jjwt-api:0.11.5")
  runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
  implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:2.0.2")
  implementation("org.springdoc:springdoc-openapi-starter-common:2.0.2")
  implementation("org.springdoc:springdoc-openapi-security:1.6.14")
  implementation("org.apache.commons:commons-lang3:3.12.0")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo.spring30x:4.5.2")
  testImplementation("it.ozimov:embedded-redis:0.7.2")
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "17"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}

tasks {
  jacocoTestReport {
    reports {
      xml.required.set(true)
      html.required.set(true)
    }
  }
}

tasks.withType<BootJar> {
  archiveFileName.set("${project.name}.jar")
}
